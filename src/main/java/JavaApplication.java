import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
	public static void main(String[] args) {
		JCFChannelService cs = new JCFChannelService();
		JCFUserService us = new JCFUserService();
		JCFMessageService ms = new JCFMessageService(us, cs);

	}
}