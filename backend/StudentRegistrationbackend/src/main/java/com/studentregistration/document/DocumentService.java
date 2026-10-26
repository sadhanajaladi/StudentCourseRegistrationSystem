
package com.studentregistration.document;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentStorageService documentStorageService;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentVersionRepository documentVersionRepository,
            DocumentStorageService documentStorageService) {

        this.documentRepository = documentRepository;
        this.documentVersionRepository = documentVersionRepository;
        this.documentStorageService = documentStorageService;
    }

    @Transactional(readOnly = true)
    public List<DocumentVersion> getVersionHistory(Long documentId) {

        if (!documentRepository.existsById(documentId)) {
            throw new IllegalArgumentException(
                    "Document not found with ID: " + documentId);
        }

        return documentVersionRepository
                .findByDocumentIdOrderByVersionNumberDesc(documentId);
    }
    

	@Transactional
	public Document createDocument(
	        String name,
	        DocumentType type,
	        String ownerEmail) {
	
	    Document document = new Document();
	
	    document.setName(name);
	    document.setType(type);
	    document.setOwnerEmail(ownerEmail);
	    document.setCurrentVersion(1);
	
	    return documentRepository.save(document);
	}

	@Transactional
	public DocumentVersion createFirstVersion(
	        Document document,
	        String filePath,
	        String originalFileName,
	        String contentType,
	        Long fileSize,
	        String uploadedBy) {
	
	    DocumentVersion version = new DocumentVersion();
	
	    version.setDocument(document);
	    version.setVersionNumber(1);
	    version.setFilePath(filePath);
	    version.setOriginalFileName(originalFileName);
	    version.setContentType(contentType);
	    version.setFileSize(fileSize);
	    version.setUploadedBy(uploadedBy);
	
	    return documentVersionRepository.save(version);
	}
	

	@Transactional
	public Document uploadFirstVersion(
	        String name,
	        DocumentType type,
	        String ownerEmail,
	        MultipartFile file) throws IOException {
	
	    String filePath = documentStorageService.storeFile(file);
	
	    Document document = createDocument(
	            name,
	            type,
	            ownerEmail);
	
	    createFirstVersion(
	            document,
	            filePath,
	            file.getOriginalFilename(),
	            file.getContentType(),
	            file.getSize(),
	            ownerEmail);
	
	    return document;
	}
	

@Transactional
public DocumentVersion uploadNewVersion(
        Long documentId,
        MultipartFile file,
        String uploadedBy) throws IOException {
	

if (!documentRepository.existsByIdAndOwnerEmail(
        documentId, uploadedBy)) {
    throw new org.springframework.security.access.AccessDeniedException(
            "You are not allowed to modify this document.");
}


    // 1. Find the existing document
    Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Document not found with ID: " + documentId));

    // 2. Store the new file without overwriting the old file
    String filePath = documentStorageService.storeFile(file);

    // 3. Calculate the next version number
    int nextVersion = document.getCurrentVersion() + 1;

    // 4. Create a database record for this version
    DocumentVersion version = new DocumentVersion();

    version.setDocument(document);
    version.setVersionNumber(nextVersion);
    version.setFilePath(filePath);
    version.setOriginalFileName(file.getOriginalFilename());
    version.setContentType(file.getContentType());
    version.setFileSize(file.getSize());
    version.setUploadedBy(uploadedBy);

    // 5. Save the version record
    DocumentVersion savedVersion =
            documentVersionRepository.save(version);

    // 6. Update the document's current version
    document.setCurrentVersion(nextVersion);
    documentRepository.save(document);

    return savedVersion;
}
@Transactional(readOnly = true)
public DocumentVersion getDocumentVersion(
        Long documentId,
        Integer versionNumber) {

    if (!documentRepository.existsById(documentId)) {
        throw new IllegalArgumentException(
                "Document not found with ID: " + documentId);
    }

    return documentVersionRepository
            .findByDocumentIdOrderByVersionNumberDesc(documentId)
            .stream()
            .filter(version ->
                    version.getVersionNumber()
                            .equals(versionNumber))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                    "Version " + versionNumber
                    + " not found for document ID: " + documentId));
}


@Transactional(readOnly = true)
public DocumentVersion getLatestDocumentVersion(Long documentId) {

    if (!documentRepository.existsById(documentId)) {
        throw new IllegalArgumentException(
                "Document not found with ID: " + documentId);
    }

    return documentVersionRepository
            .findByDocumentIdOrderByVersionNumberDesc(documentId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                    "No versions found for document ID: " + documentId));
}


@Transactional
public Document checkOutDocument(Long documentId, String userEmail) {

    // 1. Find the document
    Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Document not found with ID: " + documentId));

    // 2. Verify ownership
    if (!document.getOwnerEmail().equals(userEmail)) {
        throw new org.springframework.security.access.AccessDeniedException(
                "You are not allowed to check out this document.");
    }

    // 3. Check whether it is already checked out
    if (Boolean.TRUE.equals(document.getCheckedOut())) {
        throw new IllegalArgumentException(
                "Document is already checked out by "
                        + document.getCheckedOutBy());
    }

    // 4. Mark it as checked out
    document.setCheckedOut(true);
    document.setCheckedOutBy(userEmail);

    // 5. Save the change
    return documentRepository.save(document);
}


@Transactional
public DocumentVersion checkInDocument(
        Long documentId,
        MultipartFile file,
        String userEmail) throws IOException {

    Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Document not found with ID: " + documentId));

    if (!document.getOwnerEmail().equals(userEmail)) {
        throw new org.springframework.security.access.AccessDeniedException(
                "You are not allowed to check in this document.");
    }

    if (!Boolean.TRUE.equals(document.getCheckedOut())) {
        throw new IllegalArgumentException(
                "Document is not checked out.");
    }

    if (!userEmail.equals(document.getCheckedOutBy())) {
        throw new org.springframework.security.access.AccessDeniedException(
                "Only the user who checked out the document can check it in.");
    }

    // Save the edited file as a new version
    DocumentVersion version =
            uploadNewVersion(documentId, file, userEmail);

    // Release the checkout lock
    document.setCheckedOut(false);
    document.setCheckedOutBy(null);
    documentRepository.save(document);

    return version;
}



@Transactional(readOnly = true)
public boolean ownsDocument(Long documentId, String ownerEmail) {
    return documentRepository
            .existsByIdAndOwnerEmail(documentId, ownerEmail);
}




}
