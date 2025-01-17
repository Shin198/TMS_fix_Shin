package com.example.starter;

import static com.example.starter.Path.*;

import java.util.logging.Logger;
import com.example.starter.handler.AuthenticationHandler;
import com.example.starter.handler.ProjectHandler;
import com.example.starter.handler.TestCaseHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;

public class MainVerticle extends AbstractVerticle {
  private static Logger logger = Logger.getLogger(MainVerticle.class.getName());
  public static final String JSON = "application/json";
  private static final String PREFIX = "/tsm/api/v1";

  @Override
  public void start() throws Exception {
    // Create a Router
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route().failureHandler(Util::failureResponse);

    // JWTAuthOptions config = new JWTAuthOptions()
    //     .addPubSecKey(new PubSecKeyOptions()
    //         .setAlgorithm("RS256")
    //         .setBuffer(
    //             "-----BEGIN PUBLIC KEY-----\n" +
    //                 "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxPSbCQY5mBKFDIn1kggv\n" +
    //                 "Wb4ChjrctqD4nFnJOJk4mpuZ/u3h2ZgeKJJkJv8+5oFO6vsEwF7/TqKXp0XDp6IH\n" +
    //                 "byaOSWdkl535rCYR5AxDSjwnuSXsSp54pvB+fEEFDPFF81GHixepIbqXCB+BnCTg\n" +
    //                 "N65BqwNn/1Vgqv6+H3nweNlbTv8e/scEgbg6ZYcsnBBB9kYLp69FSwNWpvPmd60e\n" +
    //                 "3DWyIo3WCUmKlQgjHL4PHLKYwwKgOHG/aNl4hN4/wqTixCAHe6KdLnehLn71x+Z0\n" +
    //                 "SyXbWooftefpJP1wMbwlCpH3ikBzVIfHKLWT9QIOVoRgchPU3WAsZv/ePgl5i8Co\n" +
    //                 "qwIDAQAB\n" +
    //                 "-----END PUBLIC KEY-----"))
    //     .addPubSecKey(new PubSecKeyOptions()
    //         .setAlgorithm("RS256")
    //         .setBuffer(
    //             "-----BEGIN PRIVATE KEY-----\n" +
    //                 "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDE9JsJBjmYEoUM\n" +
    //                 "ifWSCC9ZvgKGOty2oPicWck4mTiam5n+7eHZmB4okmQm/z7mgU7q+wTAXv9Oopen\n" +
    //                 "RcOnogdvJo5JZ2SXnfmsJhHkDENKPCe5JexKnnim8H58QQUM8UXzUYeLF6khupcI\n" +
    //                 "H4GcJOA3rkGrA2f/VWCq/r4fefB42VtO/x7+xwSBuDplhyycEEH2Rgunr0VLA1am\n" +
    //                 "8+Z3rR7cNbIijdYJSYqVCCMcvg8cspjDAqA4cb9o2XiE3j/CpOLEIAd7op0ud6Eu\n" +
    //                 "fvXH5nRLJdtaih+15+kk/XAxvCUKkfeKQHNUh8cotZP1Ag5WhGByE9TdYCxm/94+\n" +
    //                 "CXmLwKirAgMBAAECggEAeQ+M+BgOcK35gAKQoklLqZLEhHNL1SnOhnQd3h84DrhU\n" +
    //                 "CMF5UEFTUEbjLqE3rYGP25mdiw0ZSuFf7B5SrAhJH4YIcZAO4a7ll23zE0SCW+/r\n" +
    //                 "zr9DpX4Q1TP/2yowC4uGHpBfixxpBmVljkWnai20cCU5Ef/O/cAh4hkhDcHrEKwb\n" +
    //                 "m9nymKQt06YnvpCMKoHDdqzfB3eByoAKuGxo/sbi5LDpWalCabcg7w+WKIEU1PHb\n" +
    //                 "Qi+RiDf3TzbQ6TYhAEH2rKM9JHbp02TO/r3QOoqHMITW6FKYvfiVFN+voS5zzAO3\n" +
    //                 "c5X4I+ICNzm+mnt8wElV1B6nO2hFg2PE9uVnlgB2GQKBgQD8xkjNhERaT7f78gBl\n" +
    //                 "ch15DRDH0m1rz84PKRznoPrSEY/HlWddlGkn0sTnbVYKXVTvNytKSmznRZ7fSTJB\n" +
    //                 "2IhQV7+I0jeb7pyLllF5PdSQqKTk6oCeL8h8eDPN7awZ731zff1AGgJ3DJXlRTh/\n" +
    //                 "O6zj9nI8llvGzP30274I2/+cdwKBgQDHd/twbiHZZTDexYewP0ufQDtZP1Nk54fj\n" +
    //                 "EpkEuoTdEPymRoq7xo+Lqj5ewhAtVKQuz6aH4BeEtSCHhxy8OFLDBdoGCEd/WBpD\n" +
    //                 "f+82sfmGk+FxLyYkLxHCxsZdOb93zkUXPCoCrvNRaUFO1qq5Dk8eftGCdC3iETHE\n" +
    //                 "6h5avxHGbQKBgQCLHQVMNhL4MQ9slU8qhZc627n0fxbBUuhw54uE3s+rdQbQLKVq\n" +
    //                 "lxcYV6MOStojciIgVRh6FmPBFEvPTxVdr7G1pdU/k5IPO07kc6H7O9AUnPvDEFwg\n" +
    //                 "suN/vRelqbwhufAs85XBBY99vWtxdpsVSt5nx2YvegCgdIj/jUAU2B7hGQKBgEgV\n" +
    //                 "sCRdaJYr35FiSTsEZMvUZp5GKFka4xzIp8vxq/pIHUXp0FEz3MRYbdnIwBfhssPH\n" +
    //                 "/yKzdUxcOLlBtry+jgo0nyn26/+1Uyh5n3VgtBBSePJyW5JQAFcnhqBCMlOVk5pl\n" +
    //                 "/7igiQYux486PNBLv4QByK0gV0SPejDzeqzIyB+xAoGAe5if7DAAKhH0r2M8vTkm\n" +
    //                 "JvbCFjwuvhjuI+A8AuS8zw634BHne2a1Fkvc8c3d9VDbqsHCtv2tVkxkKXPjVvtB\n" +
    //                 "DtzuwUbp6ebF+jOfPK0LDuJoTdTdiNjIcXJ7iTTI3cXUnUNWWphYnFogzPFq9CyL\n" +
    //                 "0fPinYmDJpkwMYHqQaLGQyg=\n" +
    //                 "-----END PRIVATE KEY-----"));

    // JWTAuth provider = JWTAuth.create(vertx, config);

    // router.post(PREFIX + LOGIN.toString()).handler(ctx -> AuthenticationHandler.login(ctx, provider));

    // router.route().handler(rc -> {
    //   Cookie cookie = rc.request().getCookie("token");
    //   String auth = cookie == null ? null : cookie.getValue();
    //   if (auth != null && !auth.isEmpty()) {
    //     rc.request().headers().add(String.valueOf(HttpHeaders.AUTHORIZATION), "Bearer " + auth);
    //   }
    //   String path = rc.request().path();
    //   if (path.contains(LOGOUT.toString()) && rc.request().method() == HttpMethod.DELETE) { // logout
    //     cookie.setMaxAge(0);
    //     rc.response().setStatusCode(204).end();
    //   } else {
    //     JWTAuthHandler.create(provider).handle(rc);
    //   }
    // });

    // PROJECT
    router.get(PREFIX + PROJECT.toString()).handler(ProjectHandler::getProjects);
    router.post(PREFIX + PROJECT.toString()).handler(ProjectHandler::addProject);
    router.get(PREFIX + PROJECT.toString() + "/:projectId").handler(ProjectHandler::getProjectById);

    // TEST CASE
    router.post(PREFIX + TEST_CASE.toString()).handler(TestCaseHandler::addTestCase);

    // Create the HTTP server
    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8888)
        .onSuccess(server -> logger.info("HTTP server started on port " + server.actualPort()));
  }
}
