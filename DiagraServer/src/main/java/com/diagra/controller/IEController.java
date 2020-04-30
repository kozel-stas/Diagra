package com.diagra.controller;

import com.diagra.ImportExportManager;
import com.diagra.Resource;
import com.diagra.TransitionService;
import com.diagra.dto.IERequest;
import com.diagra.dto.IEResponse;
import com.diagra.utils.AuthUtil;
import com.diagra.utils.TransitionUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/ie")
public class IEController {

    private final TransitionService transitionService;
    private final ImportExportManager importExportManager;

    public IEController(TransitionService transitionService, ImportExportManager importExportManager) {
        this.transitionService = transitionService;
        this.importExportManager = importExportManager;
    }

    @RequestMapping(
            method = {RequestMethod.POST},
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<IEResponse> convert(@RequestBody @Valid IERequest request, Authentication auth) throws IOException {
        TransitionService.Resource resource = transitionService.load(TransitionUtil.subTransitionLink(request.getTransitionLink()), AuthUtil.getUserId(auth));
        if (resource == null) {
            return ResponseEntity.badRequest().build();
        }
        Resource converted = importExportManager.convert(new Resource(request.getFrom().getResourceType(), request.getFrom().getResourceType().fromResource(resource.getResource())), request.getTo().getResourceType());

        String link = transitionService.generateTransitionLink(new TransitionService.Resource(AuthUtil.getUserId(auth), converted.getResourceType().getMediaType(), converted.getResourceType().toResource(converted.getObject())));
        IEResponse ieResponse = new IEResponse();
        ieResponse.setFormat(request.getTo());
        ieResponse.setTransitionLink(TransitionUtil.generateTransitionLink(link));
        return ResponseEntity.ok(ieResponse);
    }

}
