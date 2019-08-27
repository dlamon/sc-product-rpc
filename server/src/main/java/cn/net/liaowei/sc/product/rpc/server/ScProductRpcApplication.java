package cn.net.liaowei.sc.product.rpc.server;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSwagger2Doc
public class ScProductRpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScProductRpcApplication.class, args);
    }

}
