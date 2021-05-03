package com.github.xukaiquan.course.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.xukaiquan.course.configuration.Config;
import com.github.xukaiquan.course.dao.UserRepository;
import com.github.xukaiquan.course.model.HttpException;
import com.github.xukaiquan.course.model.Session;
import com.github.xukaiquan.course.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static com.github.xukaiquan.course.configuration.Config.UserInterceptor.COOKIE_NAME;

@RestController
@RequestMapping("api/v1/")
public class AuthController {
    private BCrypt.Hasher hasher = BCrypt.withDefaults();
    private BCrypt.Verifyer verifyer = BCrypt.verifyer();


    @Autowired
    UserRepository userRepository;
    /**
     * @api {post} /api/v1/user 注册
     * @apiName 注册
     * @apiGroup 登录与鉴权
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/x-www-form-urlencoded
     *
     * @apiParam {String} username 用户名
     * @apiParam {String} password 密码
     * @apiParamExample Request-Example:
     *          username: Alice
     *          password: MySecretPassword
     *
     * @apiSuccess {Integer} id 用户id
     * @apiSuccess {String} username 用户名
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 201 Created
     *     {
     *         "id": 123,
     *         "username": "Alice"
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求包含错误
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 400 Bad Request
     *     {
     *       "message": "Bad Request"
     *     }
     */
    /**
     * @param username 用户名
     * @param password 密码
     */
    @PostMapping("/user")
    public User register(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         HttpServletResponse response) {
        if (!StringUtils.hasLength(username) || username.length() > 20 || username.length() < 6) {
            throw new HttpException(400, "用户名必须在6到20之间");
        }
        if (!StringUtils.hasLength(password)) {
            throw new HttpException(400, "密码不能为空");
        }

        User user = new User();
        user.setUsername(username);
        //1.数据库绝对不能明文存密码
        //2.不要自己设计加密算法
        user.setEncryptedPassword(hasher
                .hashToString(12, password.toCharArray()));
        try {
            userRepository.save(user);
        } catch (Throwable e) {
            throw new RuntimeException(e);
//            throw new HttpException(409, "用户名已经被注册");
        }
        response.setStatus(201);

        return user;
    }

    /**
     * @api {post} /api/v1/session 登录
     * @apiName 登录
     * @apiGroup 登录与鉴权
     *
     * @apiHeader {String} Accept application/json
     * @apiHeader {String} Content-Type application/x-www-form-urlencoded
     *
     * @apiParam {String} username 用户名
     * @apiParam {String} password 密码
     * @apiParamExample Request-Example:
     *          username: Alice
     *          password: MySecretPassword
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 201 Created
     *     {
     *       "user": {
     *           "id": 123,
     *           "username": "Alice"
     *       }
     *     }
     *
     * @apiError 400 Bad Request 若用户的请求包含错误
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 400 Bad Request
     *     {
     *       "message": "Bad Request"
     *     }
     */
    /**
     * @param username 用户名
     * @param password 密码
     */
    @PostMapping("/session")
    public User login(@RequestParam("username") String username,
                      @RequestParam("password") String password,
                      HttpServletResponse response) {
        User user = userRepository.findUsersByUsername(username);
        if (user == null) {
            throw new HttpException(401, "登录失败，用户名或密码不正确");
        } else {
            if (verifyer.verify(password.toCharArray(), user.getEncryptedPassword()).verified) {
                String cookie = UUID.randomUUID().toString();
                response.addCookie(new Cookie(COOKIE_NAME, cookie));
                return user;
            } else {
                throw new HttpException(401, "登录失败，用户名或密码不正确");
            }
        }
    }

    /**
     * @api {get} /api/v1/session 检查登录状态
     * @apiName 检查登录状态
     * @apiGroup 登录与鉴权
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiParamExample Request-Example:
     *            GET /api/v1/auth
     *
     * @apiSuccess {User} user 用户信息
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "user": {
     *           "id": 123,
     *           "username": "Alice"
     *       }
     *     }
     * @apiError 401 Unauthorized 若用户未登录
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     * @return 已登录的用户
     */
    @GetMapping("/session")
    public Session authStatus() {
        User currentUser = Config.UserContext.getCurrentUser();
        if (currentUser == null) {
            throw new HttpException(401, "Unauthorized");
        } else {
            Session session = new Session();
            session.setUser(currentUser);
            return session;
        }
    }
    /**
     * @api {delete} /api/v1/session 登出
     * @apiName 登出
     * @apiGroup 登录与鉴权
     *
     * @apiHeader {String} Accept application/json
     *
     * @apiParamExample Request-Example:
     *            DELETE /api/v1/session
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 204 No Content
     * @apiError 401 Unauthorized 若用户未登录
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 401 Unauthorized
     *     {
     *       "message": "Unauthorized"
     *     }
     */
    /**
     * @return 已登录的用户
     */
    @DeleteMapping("/session")
    public User logout() {
        return null;
    }
}
