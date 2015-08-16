package cn.momia.service.base.web.ctrl;

import cn.momia.service.base.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorController
{
    @RequestMapping(value = "/400")
    public ResponseMessage badRequest()
    {
        return ResponseMessage.BAD_REQUEST;
    }

    @RequestMapping(value = "/403")
    public ResponseMessage forbidden()
    {
        return ResponseMessage.FORBIDDEN;
    }

    @RequestMapping(value = "/404")
    public ResponseMessage notFound()
    {
        return ResponseMessage.NOT_FOUND;
    }

    @RequestMapping(value = "/405")
    public ResponseMessage methodNotAllowed()
    {
        return ResponseMessage.METHOD_NOT_ALLOWED;
    }

    @RequestMapping(value = "/500")
    public ResponseMessage internalServerError()
    {
        return ResponseMessage.INTERNAL_SERVER_ERROR;
    }
}
