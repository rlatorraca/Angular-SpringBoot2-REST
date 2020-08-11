package com.rlsp.moneyapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.rlsp.moneyapi.config.property.RlspMoneyApiProperty;

@PropertySource(value = { "file://${HOME}/.cervejaria-s3.properties" }, ignoreResourceNotFound = true)
@Configuration
public class S3Config {
	
	@Autowired
	private RlspMoneyApiProperty property;	
	  
	 public AmazonS3 amazonS3() {
		  
		 AWSCredentials credenciais = new BasicAWSCredentials(
					property.getS3().getAccessKeyId(), property.getS3().getSecretAccessKey());
		  //BasicAWSCredentials credenciais = new  BasicAWSCredentials(property.getS3().getAccessKeyId(), property.getS3().getSecretAccessKey());
		  
			AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credenciais))
					.build();
			
			if (!amazonS3.doesBucketExistV2(property.getS3().getBucket())) { // Se nao exisitr sera criado um bucket no S3
				amazonS3.createBucket(
						new CreateBucketRequest(property.getS3().getBucket()));
				
				//Regra para arquivos temporarios
				BucketLifecycleConfiguration.Rule regraExpiracao = new BucketLifecycleConfiguration.Rule()
						.withId("Regra de expiração de arquivos temporários")
						.withFilter(new LifecycleFilter(new LifecycleTagPredicate(new Tag("expirar", "true"))))
						.withExpirationInDays(1)
						.withStatus(BucketLifecycleConfiguration.ENABLED); //habilita a regra
				
				BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
						.withRules(regraExpiracao);
				
				amazonS3.setBucketLifecycleConfiguration(property.getS3().getBucket(), 
						configuration);
			}			
			
		  return amazonS3; 
		  }
		 
}

