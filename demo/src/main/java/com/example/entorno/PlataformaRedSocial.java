
package com.example.entorno;

import java.util.HashMap;
import java.util.Map;

public class PlataformaRedSocial {

    private Map<String, Posteo> posteos;

    public PlataformaRedSocial() {
        this.posteos = new HashMap<>();
    }

    public boolean registrarPosteo(Posteo posteo) {
        if (posteo == null || posteo.getId() == null) {
            return false;
        }

        if (posteos.containsKey(posteo.getId())) {
            return false;
        }

        posteos.put(posteo.getId(), posteo);
        return true;
    }

    public Posteo obtenerPosteo(String id) {
        return posteos.get(id);
    }
}
