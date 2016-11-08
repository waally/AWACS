package io.awacs.plugin.test;

import io.awacs.core.Configuration;
import io.awacs.core.EnableInjection;
import io.awacs.core.InitializationException;
import io.awacs.core.PluginHandler;
import io.awacs.core.transport.Message;
import io.awacs.core.util.LoggerPlus;
import io.awacs.core.util.LoggerPlusFactory;
import io.awacs.repository.MongoRepository;
import org.bson.Document;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * Created by wangli on 2016/10/26.
 */
@EnableInjection
public class TestHandler implements PluginHandler {

    private static LoggerPlus logger = LoggerPlusFactory.getLogger(TestHandler.class);

    @Resource
    private MongoRepository mongoRepository;

    @Override
    public Message handle(Message message, InetSocketAddress address) {
        logger.info("insert test document begin" );
        try {
            String collection = "key_" + message.getKey();
            Document doc = Document.parse(new String(message.body()));
            doc.put("ip", address.getAddress().getHostAddress());
            doc.put("pid", message.getPid());
            mongoRepository.save(collection, doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("insert test document end" );
        return null;
    }

    @Override
    public void init(Configuration configuration) throws InitializationException {

    }
}
