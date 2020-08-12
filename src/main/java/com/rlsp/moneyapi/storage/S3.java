package com.rlsp.moneyapi.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.rlsp.moneyapi.config.property.RlspMoneyApiProperty;

@Component
public class S3 {
	
	private static final Logger logger = LoggerFactory.getLogger(S3.class);
	
	@Autowired
	private RlspMoneyApiProperty property;
	
	@Autowired
	private AmazonS3 amazonS3;
	

	
	public String salvarTemporariamente(MultipartFile arquivo) {
		
		//Libera a permissao de leitura dentro do S3 para todos os usuarios
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		//Metadados que serao usado dentro do Bucket
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(arquivo.getContentType()); // Tipo do Conteudo
		objectMetadata.setContentLength(arquivo.getSize()); // tamanho do Arquivo
		
		String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename()); // Nome Unico para o arquivo
		
		try {
		
			//Cria a requisicao do Objeto do S3
			PutObjectRequest putObjectRequest = new PutObjectRequest(
					property.getS3().getBucket(), // nome do Bucket
					nomeUnico, // Nome do Objeto (arquivo)
					arquivo.getInputStream(),  // bits do objeto
					objectMetadata) // Metadados do objeto
					.withAccessControlList(acl);
			
			//Coloca a tag de arquivo temporario
			putObjectRequest.setTagging(new ObjectTagging(
					Arrays.asList(new Tag("expirar", "true"))));
			
			//Envia para o S3
			amazonS3.putObject(putObjectRequest);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Arquivo {} enviado com sucesso para o S3.", 
						arquivo.getOriginalFilename());
			}
			
			return nomeUnico;
		} catch (IOException e) {
			throw new RuntimeException("Problemas ao tentar enviar o arquivo para o S3.", e);
		}
	}


	public String configurarUrl(String nome) {
		
		return "\\\\" + property.getS3().getBucket() + ".s3.amazonaws.com/" + nome;
	}

	public void salvar(String anexo) {
		//remove o arquivo que ja este temporariamente no S3, retirando a TAG de data de expiracao
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
				property.getS3().getBucket(), 
				anexo, 
				new ObjectTagging(Collections.emptyList())); // Passa uma lista vazia sobrescrevendo uma tag
		
		amazonS3.setObjectTagging(setObjectTaggingRequest);
	}
	
	public void remover(String objeto) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				property.getS3().getBucket(), objeto);
		
		amazonS3.deleteObject(deleteObjectRequest);
	}
	
	public void substituir(String objetoAntigo, String objetoNovo) {
		if (StringUtils.hasText(objetoAntigo)) {
			this.remover(objetoAntigo);
		}
		
		salvar(objetoNovo);
	}

	/**
	 * Gera o Nome UNICO do Arquivo
	 */
	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + "_" + originalFilename;
	}
}
