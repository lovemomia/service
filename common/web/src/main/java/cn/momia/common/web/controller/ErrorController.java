package cn.momia.common.web.controller;

import cn.momia.common.web.response.ErrorCode;
import cn.momia.common.web.response.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorController
{
    @RequestMapping(value = "/400")
    public ResponseMessage badRequest()
    {
        return new ResponseMessage(ErrorCode.BAD_REQUEST, "400 bad request");
    }

    @RequestMapping(value = "/403")
    public ResponseMessage forbidden()
    {
        return new ResponseMessage(ErrorCode.FORBIDDEN, "403 forbidden");
    }

    @RequestMapping(value = "/404")
    public ResponseMessage notFound()
    {
        return new ResponseMessage(ErrorCode.NOT_FOUND, "404 not found");
    }

    @RequestMapping(value = "/405")
    public ResponseMessage methodNotAllowed()
    {
        return new ResponseMessage(ErrorCode.METHOD_NOT_ALLOWED, "405 method not allowed");
    }

    @RequestMapping(value = "/500")
    public ResponseMessage internalServerError()
    {
        return new ResponseMessage(ErrorCode.INTERNAL_SERVER_ERROR, "500 internal server error");
    }
}
