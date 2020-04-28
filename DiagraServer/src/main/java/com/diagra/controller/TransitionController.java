package com.diagra.controller;

import com.diagra.TransitionService;
import com.diagra.dto.TransitionResponse;
import com.diagra.utils.TransitionUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(TransitionUtil.TRANSITION_LINK_CONTROLLER)
public class TransitionController {

    private final TransitionService transitionService;

    public TransitionController(TransitionService transitionService) {
        this.transitionService = transitionService;
    }

    @RequestMapping(
            value = "/{link}",
            method = RequestMethod.GET
    )
    private ResponseEntity<Resource> load(@PathVariable("link") String link) {
        TransitionService.Resource resource = transitionService.load(link);
        if (resource == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().contentType(resource.getMediaType()).body(resource.getResource());
    }

    @RequestMapping(
            method = RequestMethod.POST
    )
    private ResponseEntity<TransitionResponse> generate(@RequestParam(value = "file") MultipartFile file) {
        MediaType mediaType = MediaType.parseMediaType(file.getContentType());
        TransitionResponse transitionResponse = new TransitionResponse();
        transitionResponse.setLink(TransitionUtil.generateTransitionLink(transitionService.generateTransitionLink(new TransitionService.Resource(mediaType, file.getResource()))));
        return ResponseEntity.ok(transitionResponse);
    }

}
