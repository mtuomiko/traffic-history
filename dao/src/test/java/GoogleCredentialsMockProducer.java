import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

import org.mockito.Mockito;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
public class GoogleCredentialsMockProducer {

    @Produces
    @Singleton
    @Default
    public GoogleCredentials googleCredential() {
        return Mockito.mock(GoogleCredentials.class);
    }

    // only needed if you're injecting it inside one of your CDI beans
    @Produces
    @Singleton
    @Default
    public CredentialsProvider credentialsProvider() {
        return NoCredentialsProvider.create();
    }
}
