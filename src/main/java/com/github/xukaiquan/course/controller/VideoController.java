package com.github.xukaiquan.course.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class VideoController {
    @GetMapping("/video/{id}")
    public String getVideo(@PathVariable("id") String id) {
        String accessKeyId = ""; // 请填写您的AccessKeyId。
        String accessKeySecret = ""; // 请填写您的AccessKeySecret。
        String endpoint = ""; // 请填写您的 endpoint。
        // 填写不包含Bucket名称在内的Object完整路径。
        String objectName = "course-12345/" + id + ".mp4";
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 设置URL过期时间为1小时。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = ossClient.generatePresignedUrl("alipayss-xu", objectName, expiration);
        System.out.println(url);
        // 关闭OSSClient。
        ossClient.shutdown();
        return "<html><body><a href='" + url + "'>打开视频</a></body></html>";
    }


    @GetMapping("/course/{id}/token")
    public Object getToken(@PathVariable("id") Integer courseId) {
        String accessKeyId = ""; // 请填写您的AccessKeyId。
        String accessKeySecret = ""; // 请填写您的AccessKeySecret。
        String endpoint = ""; // 请填写您的 endpoint。
        String bucket = ""; // 请填写您的 bucketname 。
        String host = "http://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        String dir = "course-" + courseId + "/"; // 用户上传文件时指定的前缀。

        OSSClient client = new OSSClient(endpoint, new DefaultCredentialProvider(accessKeyId, accessKeySecret), null);
        long expireTimeSeconds = 30;
        long expireEndTimeMillSecond = System.currentTimeMillis() + expireTimeSeconds * 1000;
        Date expiration = new Date(expireEndTimeMillSecond);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        String postPolicy = client.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = client.calculatePostSignature(postPolicy);

        Token token = new Token();
        token.setAccessid(accessKeyId);
        token.setPolicy(encodedPolicy);
        token.setSignature(postSignature);
        token.setDir(dir);
        token.setHost(host);
        token.setExpire(expireEndTimeMillSecond / 1000);
        return token;
    }

    static class Token {
        private String accessid;
        private String host;
        private String policy;
        private String signature;
        private long expire;
        private String dir;

        public String getAccessid() {
            return accessid;
        }

        public void setAccessid(String accessid) {
            this.accessid = accessid;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPolicy() {
            return policy;
        }

        public void setPolicy(String policy) {
            this.policy = policy;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public long getExpire() {
            return expire;
        }

        public void setExpire(long expire) {
            this.expire = expire;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }

}
