/**
 * Created by Thinh-Laptop on 25.02.2017.
 */

import pro.beam.api.BeamAPI;
import pro.beam.api.resource.BeamUser;
import pro.beam.api.resource.chat.BeamChat;
import pro.beam.api.resource.chat.events.IncomingMessageEvent;
import pro.beam.api.resource.chat.events.UserJoinEvent;
import pro.beam.api.resource.chat.methods.AuthenticateMessage;
import pro.beam.api.resource.chat.methods.ChatSendMethod;
import pro.beam.api.resource.chat.replies.AuthenticationReply;
import pro.beam.api.resource.chat.replies.ReplyHandler;
import pro.beam.api.resource.chat.ws.BeamChatConnectable;
import pro.beam.api.services.impl.ChatService;
import pro.beam.api.services.impl.UsersService;

import java.util.concurrent.ExecutionException;


public class main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        BeamAPI beam = new BeamAPI("IlIBO49aTNglsEkhvhLwTsUbHW8j7gKZXtEE8sCQC0boEkjg2CSaLTHUByVDrqFo");

        BeamUser user = beam.use(UsersService.class).getCurrent().get();
        BeamChat chat = beam.use(ChatService.class).findOne(user.channel.id).get();
        BeamChatConnectable chatConnectable = chat.connectable(beam);

        if (chatConnectable.connect()) {
            chatConnectable.send(AuthenticateMessage.from(user.channel, user, chat.authkey), new ReplyHandler<AuthenticationReply>() {
                public void onSuccess(AuthenticationReply reply) {
                    chatConnectable.send(ChatSendMethod.of("Hello World!"));
                    System.out.print("Successfully connected!");
                }
                public void onFailure(Throwable var1) {
                    var1.printStackTrace();
                }
            });
        }

        chatConnectable.on(UserJoinEvent.class, event -> {
            chatConnectable.send(ChatSendMethod.of(
                    String.format("Hi %s! I'm pingbot! Write !ping and I will pong back!",
                            event.data.username)));
        });

        chatConnectable.on(IncomingMessageEvent.class, event -> {
            if (event.data.message.message.get(0).text.startsWith("!ping")) {
                chatConnectable.send(ChatSendMethod.of(String.format("@%s PONG!",event.data.userName)));
            }
        });


    }

}
