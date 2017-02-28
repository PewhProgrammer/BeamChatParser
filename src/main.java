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

    public static int tokenCount;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        tokenCount = 0 ;
        BeamAPI beam = new BeamAPI("IlIBO49aTNglsEkhvhLwTsUbHW8j7gKZXtEE8sCQC0boEkjg2CSaLTHUByVDrqFo");

        BeamUser user = beam.use(UsersService.class).getCurrent().get();
        BeamChat chat = beam.use(ChatService.class).findOne(657439).get();
        BeamUser channelUser = beam.use(UsersService.class).search("hci_livestreaming").get().get(0);
        BeamChatConnectable chatConnectable = chat.connectable(beam);

        if (chatConnectable.connect()) {
            chatConnectable.send(AuthenticateMessage.from(channelUser.channel, user, chat.authkey), new ReplyHandler<AuthenticationReply>() {
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
                    String.format("Hi %s! I'm pewhBot! I'm ready to fetch some Tokens!",
                            event.data.username)));
        });

        chatConnectable.on(IncomingMessageEvent.class, event -> {
            if (event.data.message.message.get(0).text.startsWith("!LivestreamingMeetsHCI")
                    && (event.data.userId == channelUser.id)) {

                String message = event.data.message.message.get(0).text ;
                StringBuffer target = new StringBuffer(message);
                target.replace( 0 ,23 ,"");
                String[] Tokens = target.toString().split(";");
                //chatConnectable.send(ChatSendMethod.of(String.format("Ping! %d tokens detected from %s",Tokens.length,event.data.userName)));
                for(String s:Tokens){
                    if(s.equals("NexXw5")) {
                        tokenCount++;
                        chatConnectable.send(ChatSendMethod.of(String.format("Ping! I found my token!!")));
                        continue;
                    }
                }
            }
            if (event.data.userName.contentEquals("pewhTV")) {
                if(event.data.message.message.get(0).text.startsWith("!print"))
                chatConnectable.send(ChatSendMethod.of(String.format("Ping! I've counted %d occurences on beam",tokenCount)));
                if(event.data.message.message.get(0).text.startsWith("!shutdown")){
                    chatConnectable.send(ChatSendMethod.of(String.format("Ping! I'm going offline now!")));
                    System.exit(1);
                }
            }
        });


    }

}
