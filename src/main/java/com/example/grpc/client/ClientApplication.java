package com.example.grpc.client;

import com.example.grpc.server.message.*;

import com.example.grpc.server.service.StreamServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


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
//        StreamServiceGrpc.StreamServiceStub streamService = StreamServiceGrpc.newStub(channel);
//        StreamObserver<ClientStreamRequest> request = streamService.clientStream(new StreamObserver<ClientStreamResponse>() {
//            @Override
//            public void onNext(ClientStreamResponse value) {
//                System.out.println(value.getCount());
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//
//            @Override
//            public void onCompleted() {
//
//            }
//        });
//        for (int i = 0; i < 10; i++) {
//            ClientStreamRequest value = ClientStreamRequest.newBuilder()
//                    .setNext(i)
//                    .build();
//            request.onNext(value);
//            Thread.sleep(1_000L);
//        }
//        request.onCompleted();

        //Bidirection Streaming
        AtomicReference<StreamObserver<BidirectionStreamRequest>> ref = new AtomicReference<>();
        StreamServiceGrpc.StreamServiceStub streamService = StreamServiceGrpc.newStub(channel);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StreamObserver<BidirectionStreamRequest> request = streamService.bidirectionStream(new StreamObserver<BidirectionStreamResponse>() {
            AtomicInteger count = new AtomicInteger(0);

            @Override
            public void onNext(BidirectionStreamResponse value) {
                System.out.println(value.getMsg());
                if (count.addAndGet(1) < 100) {
                    System.out.println("count = " + count + " client ping");
                    BidirectionStreamRequest x = BidirectionStreamRequest.newBuilder()
                            .setMsg("client ping")
                            .build();
                    ref.get().onNext(x);
                } else {
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onError(Throwable t) {
                countDownLatch.countDown();
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
                System.out.println("server is completed");
            }
        });
        ref.set(request);
        System.out.println("count = 0 client ping");
        request.onNext(BidirectionStreamRequest.newBuilder()
                .setMsg("client ping")
                .build());
        countDownLatch.await();
        request.onCompleted();

        Thread.sleep(1_000L);
        channel.shutdown();

    }

}
