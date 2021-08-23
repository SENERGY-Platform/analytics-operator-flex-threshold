import org.infai.ses.senergy.models.DeviceMessageModel;
import org.infai.ses.senergy.models.MessageModel;
import org.infai.ses.senergy.operators.Config;
import org.infai.ses.senergy.operators.Helper;
import org.infai.ses.senergy.operators.Message;
import org.infai.ses.senergy.operators.OperatorInterface;
import org.infai.ses.senergy.operators.flexthreshold.FlexThreshold;
import org.infai.ses.senergy.operators.flexthreshold.InputParser;
import org.infai.ses.senergy.testing.utils.JSONHelper;
import org.infai.ses.senergy.utils.ConfigProvider;
import org.json.simple.JSONArray;
import org.junit.Assert;
import org.junit.Test;

public class FlexThresholdTest {
    @Test
    public void Test() throws Exception {
        Config config = new Config(new JSONHelper().parseFile("config.json").toString());
        InputParser inputParser = new InputParser();
        inputParser.parse(config.getInputTopicsConfigs());
        JSONArray messages = new JSONHelper().parseFile("messages.json");
        String topicName = config.getInputTopicsConfigs().get(0).getName();
        ConfigProvider.setConfig(config);
        Message message = new Message();
        MessageModel model = new MessageModel();
        OperatorInterface testOperator = new FlexThreshold(inputParser.getInputs());
        message.addInput("expect_score");
        message.addInput("expect_score_percentage");
        message = testOperator.configMessage(message);
        for (Object msg : messages) {
            DeviceMessageModel deviceMessageModel = JSONHelper.getObjectFromJSONString(msg.toString(), DeviceMessageModel.class);
            assert deviceMessageModel != null;
            model.putMessage(topicName, Helper.deviceToInputMessageModel(deviceMessageModel, topicName));
            message.setMessage(model);
            testOperator.run(message);

            Assert.assertEquals(message.getInput("expect_score").getValue().intValue(), message.getMessage().getOutputMessage().getAnalytics().get("score"));
            Assert.assertEquals(message.getInput("expect_score_percentage").getValue().intValue(), message.getMessage().getOutputMessage().getAnalytics().get("score_percentage"));
        }
    }
}
