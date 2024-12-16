# Verifiable Credential Status Check
Implements Bitstring Status List v1.0 standard to fetch status of a Verifiable Credential

## Installation

Add the following repository to your maven pom.xml

    <repositories>
        <repository>
            <id>CredenceID-maven-release</id>
            <url>https://nexus.credenceid.com/repository/maven-releases/</url>
        </repository>
    </repositories>

Add the following dependency to your maven pom.xml

        <dependency>
            <groupId>com.credenceid</groupId>
            <artifactId>verifiable-credential-status</artifactId>
            <version>${verifiable-credential-status.version}</version>
        </dependency>

## Usage
    
    List<com.credenceid.vcstatus.dto.StatusVerificationResult> statusVerificationResults = 
    com.credenceid.vcstatus.service.StatusVerifierService.verifyStatus(List<com.danubetech.verifiablecredentials.credentialstatus> listOfCredentialStatus)
