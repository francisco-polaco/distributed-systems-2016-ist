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
cd juddi-3.3.2_tomcat-7.0.64_909/bin
./startup.sh
```


[2] Criar pasta temporária

```
cd                        ????
mkdir A-43
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone https://github.com/tecnico-distsys/A_43-project         ALTERAR ISTO APOS TAG
```
*(colocar aqui comandos git para obter a versão entregue a partir da tag e depois apagar esta linha)*


[4] Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

```
cd ...                       ???
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

...


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**

```
cd A_43-project/broker-ws
mvn clean generate-sources install
mvn exec:java
```


[2] Construir **cliente** e executar testes

```
cd A_43-project/broker-ws-cli
mvn clean generate-sources install
```

...

-------------------------------------------------------------------------------
**FIM**
