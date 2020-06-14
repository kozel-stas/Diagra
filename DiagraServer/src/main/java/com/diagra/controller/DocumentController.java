package com.diagra.controller;

import com.diagra.ImportExportManager;
import com.diagra.TransitionService;
import com.diagra.dao.model.AccessType;
import com.diagra.dao.model.AlgorithmScheme;
import com.diagra.dto.DocumentDto;
import com.diagra.dto.IEType;
import com.diagra.logic.algorithm.AlgorithmSchemeManager;
import com.diagra.mapper.DocumentMapper;
import com.diagra.utils.AuthUtil;
import com.diagra.utils.CustomDeferredResult;
import com.diagra.utils.DefaultResponseCallback;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final AlgorithmSchemeManager algorithmSchemeManager;
    private final TransitionService transitionService;
    private final ImportExportManager importExportManager;
    private final DocumentMapper documentMapper;

    public DocumentController(AlgorithmSchemeManager algorithmSchemeManager, TransitionService transitionService, ImportExportManager importExportManager, DocumentMapper documentMapper) {
        this.algorithmSchemeManager = algorithmSchemeManager;
        this.transitionService = transitionService;
        this.importExportManager = importExportManager;
        this.documentMapper = documentMapper;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public DeferredResult<ResponseEntity<DocumentDto>> store(@RequestBody @Valid DocumentDto documentDto, Authentication auth) throws IOException {
        String userID = AuthUtil.getUserId(auth);
        documentDto.setUserID(userID);
        AlgorithmScheme algorithmScheme = documentMapper.fromDto(documentDto);
        algorithmScheme.setOwnerID(userID);
        Map<String, AccessType> map = new HashMap<>();
        map.put(userID, AccessType.WRITE);
        algorithmScheme.setAccessTypes(map);
        CustomDeferredResult<ResponseEntity<DocumentDto>> result = new CustomDeferredResult<>();
        algorithmSchemeManager.store(algorithmScheme).addCallback(new DefaultResponseCallback<AlgorithmScheme, DocumentDto>(result) {

            @Override
            public void onSuccess(AlgorithmScheme algorithmScheme) {
                result.setResult(ResponseEntity.ok(documentMapper.toDto(algorithmScheme)));
            }
        });
        return result;
    }

    @RequestMapping(
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public DeferredResult<ResponseEntity<List<DocumentDto>>> read(@RequestParam(value = "type", defaultValue = "XML") IEType ieType, Authentication auth) throws IOException {
        String userID = AuthUtil.getUserId(auth);
        CustomDeferredResult<ResponseEntity<List<DocumentDto>>> result = new CustomDeferredResult<>();
        algorithmSchemeManager.get(userID).addCallback(new DefaultResponseCallback<List<AlgorithmScheme>, List<DocumentDto>>(result) {
            @Override
            public void onSuccess(List<AlgorithmScheme> algorithmSchemes) {
                List<DocumentDto> list = new ArrayList<>(algorithmSchemes.size());
                for (AlgorithmScheme algorithmScheme : algorithmSchemes) {
                    list.add(documentMapper.toDto(algorithmScheme));
                }
                result.setResult(ResponseEntity.ok(list));
            }
        });
        return result;
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public DeferredResult<ResponseEntity<DocumentDto>> get(@PathVariable("id") String id, @RequestParam(value = "type", defaultValue = "XML") IEType ieType, Authentication auth) throws IOException {
        String userID = AuthUtil.getUserId(auth);
        CustomDeferredResult<ResponseEntity<DocumentDto>> result = new CustomDeferredResult<>();
        algorithmSchemeManager.get(userID, id).addCallback(new DefaultResponseCallback<AlgorithmScheme, DocumentDto>(result) {
            @Override
            public void onSuccess(AlgorithmScheme algorithmScheme) {
                result.setResult(ResponseEntity.ok(documentMapper.toDto(algorithmScheme)));
            }
        });
        return result;
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    public DeferredResult<ResponseEntity<Object>> delete(@PathVariable("id") String id, Authentication auth) {
        String userID = AuthUtil.getUserId(auth);
        CustomDeferredResult<ResponseEntity<Object>> result = new CustomDeferredResult<>();
        algorithmSchemeManager.delete(id, userID).addCallback(new DefaultResponseCallback<Void, Object>(result) {

            @Override
            public void onSuccess(Void aVoid) {
                result.setResult(ResponseEntity.ok().build());
            }
        });
        return result;
    }

}
