package com.example.grpc.client;

import com.example.grpc.server.message.HelloRequest;
import com.example.grpc.server.message.HelloResponse;
import com.example.grpc.server.service.HelloServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ClientApplication.class, args);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
        HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(channel);

        HelloResponse response = helloService.hello(HelloRequest.newBuilder().setFirstName("minh").setLastName("tuan").build());
        System.out.println(response);
        Thread.sleep(30_000);
        channel.shutdown();

    }

}
