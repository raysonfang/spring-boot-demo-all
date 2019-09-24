package cn.raysonblog.quartz.core.exception;

/**
 * <p>Title: ServiceException</p>
 * <p>Description: 业务层异常类</p>
 * <p>Company: </p>
 *
 * @author rayson
 * @version 1.0
 * @date 2018-06-20 17:57
 */
public class ServiceException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7432064685204385795L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException() {

    }

}
