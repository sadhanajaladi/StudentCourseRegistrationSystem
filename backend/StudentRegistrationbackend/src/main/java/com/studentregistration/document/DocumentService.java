
package com.studentregistration.document;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


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



}
