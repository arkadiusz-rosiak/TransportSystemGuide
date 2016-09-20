package pl.rosiakit.model;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-20
 */
public class JsonResponse {

    @JsonView(JsonViewsContainer.ResponseView.class)
    private int code = 500;

    @JsonView(JsonViewsContainer.ResponseView.class)
    private Object response;

    public JsonResponse(int code, Object response) {
        this.code = code;
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public Object getResponse() {
        return response;
    }
}
