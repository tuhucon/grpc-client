package com.example.grpc.client;

import com.example.grpc.server.message.HelloRequest;
import com.example.grpc.server.message.HelloResponse;
import com.example.grpc.server.message.ServerStreamRequest;
import com.example.grpc.server.message.ServerStreamResponse;
import com.example.grpc.server.service.HelloServiceGrpc;

import com.example.grpc.server.service.StreamServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.function.Consumer;


@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ClientApplication.class, args);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
//        HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(channel);

//        HelloResponse response = helloService.hello(HelloRequest.newBuilder().setFirstName("minh").setLastName("tuan").build());
//        System.out.println(response);


        StreamServiceGrpc.StreamServiceBlockingStub streamService = StreamServiceGrpc.newBlockingStub(channel);
        streamService.serverStream(ServerStreamRequest.newBuilder().setStart(10).build()).forEachRemaining(
                serverStreamResponse -> System.out.println(serverStreamResponse.getNext())
        );

        Thread.sleep(30_000);
        channel.shutdown();

    }

}
