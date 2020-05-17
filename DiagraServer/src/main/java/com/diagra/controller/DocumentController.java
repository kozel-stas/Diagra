package com.diagra.controller;

import com.diagra.ImportExportManager;
import com.diagra.Resource;
import com.diagra.ResourceType;
import com.diagra.TransitionService;
import com.diagra.dao.model.AlgorithmScheme;
import com.diagra.dto.DocumentDto;
import com.diagra.logic.algorithm.AlgorithmSchemeManager;
import com.diagra.utils.AuthUtil;
import com.diagra.utils.TransitionUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;


@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final AlgorithmSchemeManager algorithmSchemeManager;
    private final TransitionService transitionService;
    private final ImportExportManager importExportManager;

    public DocumentController(AlgorithmSchemeManager algorithmSchemeManager, TransitionService transitionService, ImportExportManager importExportManager) {
        this.algorithmSchemeManager = algorithmSchemeManager;
        this.transitionService = transitionService;
        this.importExportManager = importExportManager;
    }

    public ResponseEntity<DocumentDto> store(DocumentDto documentDto, Authentication auth) throws IOException {
        String userID = AuthUtil.getUserId(auth);
        return null;
    }

}
