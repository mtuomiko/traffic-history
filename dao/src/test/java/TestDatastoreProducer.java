import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;


//@Priority(1)
//@Alternative
//@ApplicationScoped
//public class TestDatastoreProducer {
//    @Produces
//    Datastore datastore() {
//        return DatastoreOptions.newBuilder()
//                .setHost("http://host.docker.internal:8000")
//                .setProjectId("traffic-history-376700")
//                .build().getService();
//    }
//}
