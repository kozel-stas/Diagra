package com.diagra.controller;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.MimeType;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@RequestMapping("/api/export")
@RestController
public class ExportController {

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public void exportToPNG(@RequestBody @Valid Export export, HttpServletResponse response) throws IOException, TranscoderException {
        TranscoderInput input_svg_image = new TranscoderInput(IOUtils.toInputStream(export.data, StandardCharsets.UTF_8));
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        OutputStream png_ostream = response.getOutputStream();
        TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
        PNGTranscoder my_converter = new PNGTranscoder();
        my_converter.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) export.getHeight());
        my_converter.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) export.getWidth());
        my_converter.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);
        // Step-4: Convert and Write output
        my_converter.transcode(input_svg_image, output_png_image);
        // Step 5- close / flush Output Stream
        response.setStatus(HttpServletResponse.SC_OK);
        png_ostream.flush();
        png_ostream.close();

    }

    public static final class Export {

        @NotNull
        private String data;
        private int width;
        private int height;

        public void setData(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

}
