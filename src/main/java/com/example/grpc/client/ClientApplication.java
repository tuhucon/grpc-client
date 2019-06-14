package com.example.grpc.client;

import com.example.grpc.server.message.ClientStreamRequest;
import com.example.grpc.server.message.ClientStreamResponse;
import com.example.grpc.server.message.ServerStreamRequest;
import com.example.grpc.server.service.StreamServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ClientApplication.class, args);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();

        //Unary call
//        HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(channel);

//        HelloResponse response = helloService.hello(HelloRequest.newBuilder().setFirstName("minh").setLastName("tuan").build());
//        System.out.println(response);

        //Server Streaming
//        StreamServiceGrpc.StreamServiceBlockingStub blockingStreamService = StreamServiceGrpc.newBlockingStub(channel);
//        blockingStreamService.serverStream(ServerStreamRequest.newBuilder().setStart(10).build()).forEachRemaining(
//                serverStreamResponse -> System.out.println(serverStreamResponse.getNext())
//        );


        //Client Streaming
        StreamServiceGrpc.StreamServiceStub streamService = StreamServiceGrpc.newStub(channel);
        StreamObserver<ClientStreamRequest> request = streamService.clientStream(new StreamObserver<ClientStreamResponse>() {
            @Override
            public void onNext(ClientStreamResponse value) {
                System.out.println(value.getCount());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });
        for (int i = 0; i < 10; i++) {
            ClientStreamRequest value = ClientStreamRequest.newBuilder()
                    .setNext(i)
                    .build();
            request.onNext(value);
            Thread.sleep(1_000L);
        }
        request.onCompleted();

        Thread.sleep(30_000);
        channel.shutdown();

    }

}
