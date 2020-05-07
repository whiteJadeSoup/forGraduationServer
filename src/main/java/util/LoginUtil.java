package util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;


public class LoginUtil {
    /**
     * 标记登录状态
     */
    public static void markAsLogin(Channel channel){
        channel.attr(Attributes.LOGIN).set(true);
    }
    
    
    public static void markAsLogout(Channel channel) {
    	channel.attr(Attributes.LOGIN).set(false);
    }
    
    public static boolean hasLogin(Channel channel){
        Attribute<Boolean> login =  channel.attr(Attributes.LOGIN);
        
        //标志位为true
        if ((login.get() != null) && (login.get() == true) )
            return true;
        return false;
    }
}