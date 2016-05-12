# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 43 - Campus Alameda

João Serras 79664 joao.serras@ist.utl.pt

Daniel Caramujo 79714 daniel.caramujo@ist.utl.pt

Francisco Santos 79719 franciscopolaco@tecnico.ulisboa.pt


Repositório:
[tecnico-distsys/A_43-project](https://github.com/tecnico-distsys/A_43-project/)


-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo

Linux


[1] Iniciar servidores de apoio

JUDDI:
```
juddi-startup
```


[2] Criar pasta temporária

```
cd
mkdir A_43-project
cd A_43-project
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone --branch SD_R2 https://github.com/tecnico-distsys/A_43-project
```


[4] Instalar módulos de bibliotecas auxiliares

```
cd A_43-project
cd uddi-naming
mvn clean install
```

[5] Instalar módulos de bibliotecas auxiliares

Estão guardados por defeito 3 KeyStores  e certificados de transportadoras e um para broker no próprio repositório, para acelerar a demonstração.
No entanto, para experimentar com outras chaves,  será necessário gerar os Certificados e os KeyStores com o script fornecido.
Após isto, copiar os ficheiros .cer e da CA para a raiz de /ca-ws.
Depois copiar os .jks para o serviço correspondente.
As instruções de uso do script estão nele próprio.

```
wget http://web.ist.utl.pt/~ist179719/sd/gen_keys.sh
sh gen_keys.sh
```

-------------------------------------------------------------------------------

### Serviço CA

[1] Construir e executar **servidor**

```
cd A_43-project/ca-ws
mvn clean install
mvn exec:java
```

[2] Construir **cliente** e executar testes

```
cd A_43-project/ca-ws-cli
mvn clean generate-sources install
```

-------------------------------------------------------------------------------

### Handlers

[1] Construir

```
cd A_43-project/ws-handlers
mvn clean install
```


-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar **servidor**

```
cd A_43-project/transporter-ws
mvn clean generate-sources install
mvn exec:java
```

[2] Construir **cliente** e executar testes

```
cd A_43-project/transporter-ws-cli
mvn clean generate-sources install
```


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir **cliente**

```
cd A_43-project/broker-ws-cli
mvn clean generate-sources install -Dmaven.test.skip=true
```

[2] Construir e executar **servidor** de backup

```
cd A_43-project/broker-ws
mvn clean generate-sources install
mvn -Dws.i=2 exec:java
```

[3] Executar **servidor**

```
cd A_43-project/broker-ws
mvn exec:java
```

[4] Executar testes do **cliente**

```
cd A_43-project/broker-ws-cli
mvn verify
```


-------------------------------------------------------------------------------
**FIM**
