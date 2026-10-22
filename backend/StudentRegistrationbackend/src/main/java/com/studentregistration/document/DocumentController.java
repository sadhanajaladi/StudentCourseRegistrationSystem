
package com.studentregistration.document;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{documentId}/versions")
    public List<DocumentVersion> getVersionHistory(
            @PathVariable Long documentId) {

        try {
            return documentService.getVersionHistory(documentId);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage());
        }
    }
    

@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public Map<String, Object> uploadDocument(
        @RequestParam String name,
        @RequestParam DocumentType type,
        @RequestParam String ownerEmail,
        @RequestParam("file") MultipartFile file)
        throws IOException {

    Document document = documentService.uploadFirstVersion(
            name,
            type,
            ownerEmail,
            file);

    return Map.of(
            "message", "Document uploaded successfully",
            "documentId", document.getId(),
            "name", document.getName(),
            "type", document.getType(),
            "currentVersion", document.getCurrentVersion());
}

}
