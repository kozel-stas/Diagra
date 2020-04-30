package com.diagra;

import org.springframework.http.MediaType;

public interface TransitionService {

    Resource load(String transitionLink, String ownerID);

    String generateTransitionLink(Resource resource);

    class Resource {

        private final String ownerID;
        private final MediaType mediaType;
        private final org.springframework.core.io.Resource resource;

        public Resource(String ownerID, MediaType mediaType, org.springframework.core.io.Resource resource) {
            this.ownerID = ownerID;
            this.mediaType = mediaType;
            this.resource = resource;
        }

        public org.springframework.core.io.Resource getResource() {
            return resource;
        }

        public MediaType getMediaType() {
            return mediaType;
        }

        public String getOwnerID() {
            return ownerID;
        }

    }

}
