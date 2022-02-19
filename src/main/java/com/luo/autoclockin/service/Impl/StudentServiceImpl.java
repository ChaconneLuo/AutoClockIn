package com.luo.autoclockin.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.luo.autoclockin.Dao.StudentDao;
import com.luo.autoclockin.entity.Student;
import com.luo.autoclockin.service.StudentService;
import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.MessageResult;
import com.zjiecode.wxpusher.client.bean.Result;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.security.cert.X509Certificate;


@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;

    @Override
    public boolean AddUser(Student stu) {
        if (studentDao.search(stu.getStu_id()) != null) {
            return false;
        } else {
            if (check(stu.getStu_id(), stu.getUrl())) {
                studentDao.insert(stu);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void AllClockIn() throws InterruptedException {
        Queue<Student> queue = null;
        Queue<Student> errorStudent = new LinkedList<>();
        List<Student> allStudent = studentDao.getAllStudent();
        queue = new LinkedList<>(allStudent);
        while (!queue.isEmpty()) {
            Student poll = queue.poll();
            if (!SingleClockIn(poll)) {
                errorStudent.add(poll);
            } else {
                if (poll.getPushUid() != null) {
                    Message message = new Message();
                    message.setAppToken("Test");//推送密钥，自行填写
                    message.setContentType(Message.CONTENT_TYPE_TEXT);
                    message.setContent(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(System.currentTimeMillis()) + " " + poll.getStu_id() + " " + "打卡成功");
                    message.setUid(poll.getPushUid());
                    Result<List<MessageResult>> result = WxPusher.send(message);
                }
            }
        }
        if (!errorStudent.isEmpty()) {
            ErrorHandler(errorStudent);
        }
    }

    @Override
    public boolean check(String stu_id, String url) {
        if (stu_id.length() != 11 || !url.contains(new String("ehallplatform.xust.edu.cn/default/jkdk"))) {
            return false;
        }
        return true;
    }

    public boolean SingleClockIn(Student stu) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            try {
                if (post(stu.getStu_id(), stu.getUrl())) {
                    return true;
                } else {
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean post(String stu_id, String url) throws JsonProcessingException {
        String cookie = getCookie(url);
        String message = getMessage(stu_id, cookie);
        String changeMessage = changeMessage(message);

        System.out.println(sendHttps(url, changeMessage, cookie));
        return true;
    }

    private String getCookie(String url) {
        try {
            Map<String, String> map = Jsoup
                    .connect(url)
                    .sslSocketFactory(socketFactory())
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                    .header("Host", "ehallplatform.xust.edu.cn")
                    .ignoreContentType(true)
                    .execute().cookies();
            String s = map.get("JSESSIONID");
            String finalString = "JSESSIONID=" + s;
            System.out.println(finalString);
            return finalString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }

    private String getMessage(String id, String cookie) {
        String url = "https://ehallplatform.xust.edu.cn/default/jkdk/mobile/com.primeton.eos.jkdk.xkdjkdkbiz.getJkdkRownum.biz.ext?gh=";
        try {
            Document document = Jsoup
                    .connect(url + id)
                    .sslSocketFactory(socketFactory())
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                    .header("Cookie", cookie)
                    .ignoreContentType(true)
                    .post();
            String result = document.text();
            String substring = result.substring(9, result.length() - 2);
            System.out.println(substring);
            return substring;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings(value = {"deprecation"})
    private static String changeMessage(String message) throws JsonProcessingException {
        JsonMapper jsonMapper = new JsonMapper();
        JsonNode jo = jsonMapper.readTree(message);
        final ObjectNode tmpNode = JsonNodeFactory.instance.objectNode();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);


        tmpNode.put("procinstid", "");
        tmpNode.put("empid", jo.get("EMPID"));
        tmpNode.put("shzt", jo.get("SHZT"));
        tmpNode.put("id", "");
        tmpNode.put("jrrq1", new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));//
        tmpNode.put("sjh2", jo.get("SJH2"));
        tmpNode.put("jrsfzx3", jo.get("JRSFZX3"));
        tmpNode.put("szdd4", jo.get("SZDD4"));
        tmpNode.put("xxdz41", jo.get("XXDZ4_1"));
        tmpNode.put("jrtwfw5", jo.get("JRTWFW5"));
        tmpNode.put("jrsfjgwh6", jo.get("JRSFJGWH6"));
        tmpNode.put("jrsfjghb7", jo.get("JRSFJGHB7"));
        tmpNode.put("jrsfcxfrzz8", jo.get("JRSFCXFRZZ8"));
        tmpNode.put("jrsfywhrjc9", jo.get("JRSFYWHRJC9"));
        tmpNode.put("jrsfyhbrjc10", jo.get("JRSFYHBRJC10"));
        tmpNode.put("jrsfjcgrrq11", jo.get("JRSFJCGRRQ11"));
        tmpNode.put("jssfyqzysgl12", jo.get("JRSFJGHB7"));
        tmpNode.put("sfcyglq13", jo.get("SFCYGLQ13"));
        tmpNode.put("glkssj131", "");
        tmpNode.put("gljssj132", "");
        tmpNode.put("sfyyqxgzz14", jo.get("SFYYQXGZZ14"));
        tmpNode.put("qtxx15", (JsonNode) null);
        tmpNode.put("gh", jo.get("GH"));
        tmpNode.put("xm", jo.get("XM"));
        tmpNode.put("xb", jo.get("XB"));
        tmpNode.put("sfzh", "");
        tmpNode.put("szyx", jo.get("SZYX"));
        tmpNode.put("xydm", jo.get("XYDM"));
        tmpNode.put("zy", "");
        tmpNode.put("zydm", "");
        tmpNode.put("bj", jo.get("BJ"));
        tmpNode.put("bjdm", jo.get("BJDM"));
        tmpNode.put("jg", "");
        tmpNode.put("yx", "");
        tmpNode.put("sfxs", jo.get("SFXS"));
        tmpNode.put("xslx", jo.get("XSLX"));
        tmpNode.put("jingdu", jo.get("JINGDU"));
        tmpNode.put("weidu", jo.get("WEIDU"));
        tmpNode.put("guo", "中国");
        tmpNode.put("sheng", jo.get("SHENG"));
        tmpNode.put("shi", jo.get("SHI"));
        tmpNode.put("xian", jo.get("XIAN"));
        tmpNode.put("sfncxaswfx16", jo.get("SFNCXASWFX16"));
        tmpNode.put("dm", "4006078");
        tmpNode.put("jdlx", jo.get("JDLX"));
        tmpNode.put("tbsj", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//
        tmpNode.put("fcjtgj17Qt", "");
        tmpNode.put("fcjtgj17", "");
        tmpNode.put("hqddlx", jo.get("HQDDLX"));
        tmpNode.put("ymtys", "");
        tmpNode.put("time", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        System.out.println(tmpNode.toString());
        ObjectNode jb = JsonNodeFactory.instance.objectNode();
        jb.put("xkdjkdk", tmpNode);
        return jb.toString();
    }

    private String sendHttps(String stu_url, String a, String cookie) {
        String url = "https://ehallplatform.xust.edu.cn/default/jkdk/mobile/com.primeton.eos.jkdk.xkdjkdkbiz.jt.biz.ext";
        System.out.println(a);
        String result = "";
        try {
            Document document = Jsoup.connect(url).sslSocketFactory(socketFactory())
                    .method(Connection.Method.POST)
                    .requestBody(a)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Connection", "keep-alive")
                    .header("Content-Length", "930")
                    .header("Content-Type", "text/json")
                    .header("Cookie", cookie)
                    .header("Host", "ehallplatform.xust.edu.cn")
                    .header("Origin", "https://ehallplatform.xust.edu.cn")
                    .header("Referer", stu_url)
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .ignoreContentType(true)
                    .post();
            result = document.text();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    private void ErrorHandler(Queue<Student> queue) {
        System.out.println("CLOCK ERROR:\n");
        for (Student s : queue) {
            if (s.getPushUid() != null) {
                Message message = new Message();
                message.setAppToken("Test");
                message.setContentType(Message.CONTENT_TYPE_TEXT);
                message.setContent(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(System.currentTimeMillis()) + " " + s.getStu_id() + " " + "打卡失败");
                message.setUid(s.getPushUid());
                Result<List<MessageResult>> result = WxPusher.send(message);
            } else {
                System.out.println(s.getStu_id() + "Error");
            }
        }
    }
}
