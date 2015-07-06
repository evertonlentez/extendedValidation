# Tarcius
## Auditoria de atividade

### Objetivo
Tarcius � um componente que prov� uma API simplificada para registro de auditoria das atividades dos usu�rios em uma aplica��o.

Baseado nos conceitos e caracter�sticas disponibilizadas pelo CDI, possibilita o registro de chamadas a m�todos, bem como dos valores dos par�metros identificados, atribuindo refer�ncias que visam dar sentido de neg�cio �s informa��es coletadas.

Concebido para ser adapt�vel �s necessidades e particularidades de cada aplica��o, delega ao usu�rio do componente a defini��o do modelo da auditoria, a composi��o dos dados coletados no modelo definido e o envio desses ao reposit�rio escolhido para os dados de auditoria, al�m de possibilitar a defini��o de tradutores espec�ficos para os par�metros ou o uso de tradutores padr�es disponibilizados.

### Depend�ncias e Configura��o
#### Depend�ncias
Para utilizar o componente basta adicion�-lo como depend�ncia de seu projeto. A vers�o mais recente � a seguinte:
```xml
<dependency>
    <groupId>com.github.ldeitos</groupId>
    <artifactId>tarcius</artifactId>
    <version>0.1.2</version>
</dependency>
```

As principais depend�ncias do componente s�o:
 - [cdi-util 0.6.2](http://search.maven.org/#artifactdetails%7Ccom.github.ldeitos%7Ccdi-util%7C0.6.2%7Cjar)
 - [commons-collections 3.2.1](http://search.maven.org/#artifactdetails%7Corg.lucee%7Ccommons-collections%7C3.2.1%7Cbundle)
 - [commons-collections4 4.0](http://search.maven.org/#artifactdetails%7Corg.apache.commons%7Ccommons-collections4%7C4.0%7Cjar)
 - [commons-configuration 1.0](http://search.maven.org/#artifactdetails%7Ccom.github.testdriven.guice%7Ccommons-configuration%7C1.0%7Cjar)
 - [commons-lang3 3.3.2](http://search.maven.org/#artifactdetails%7Corg.apache.commons%7Ccommons-lang3%7C3.3.2%7Cjar)
 - [jaxb-api 2.2.11](http://search.maven.org/#artifactdetails%7Cjavax.xml.bind%7Cjaxb-api%7C2.2.11%7Cjar)
 - [jersey-json 1.19](http://search.maven.org/#artifactdetails%7Ccom.sun.jersey%7Cjersey-json%7C1.19%7Cjar)
 
#### Configura��o
As configura��es do componente s�o efetuadas atrav�s do arquivo **tarcius.xml**, que deve ficar localizado no diret�rio META-INF do projeto. Segue exemplo do arquivo:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<tarcius>
	<formatter-class></formatter-class>
	<dispatcher-class></dispatcher-class>
	<interrupt-on-error></interrupt-on-error>
</tarcius>
```
As configura��es poss�veis s�o:
- **formatter-class**: define a classe que implementa o formatador da auditoria, conforme poder� ser visto com mais detalhes mais adiante. Esta configura��o � obrigat�ria at� a vers�o 0.1.2; a partir dessa pode-se suprimir esta configura��o desde que haja apenas uma implementa��o do formatador dispon�vel no *classpath*. Nesta situa��o esse ser� carregado automaticamente atrav�s do CDI.
- **dispatcher-class**: define a classe que implementa o componente que destina os dados coletados durante a auditoria, conforme ser� detalhado mais adiante. Esta configura��o � obrigat�ria at� a vers�o 0.1.2; a partir dessa pode-se suprimir esta configura��o desde que haja apenas uma implementa��o do *dispatcher* dispon�vel no *classpath*. Nesta situa��o esse ser� carregado automaticamente atrav�s do CDI.
- **interrupt-on-error**: define o comportamento do componente na ocorr�ncia de exce��es durante a execu��o do processo de auditoria. A configura��o padr�o � *false*, ou seja, caso ocorram erros o processamento n�o � interrompido, apenas uma mensagem de *warning* � gravada no log da aplica��o. Caso seja configurado para *true*, a ocorr�ncia de exce��es interromper� o fluxo do processamento, sendo que a exce��o causadora da interrup��o ser� relan�ada atrav�s de uma *AuditException*. Esta configura��o � opcional.

### Modelo de uso
O mecanismo baseia-se na intercepta��o da chamada de m�todos identificados com a anota��o *@Audit* atrav�s de um *interceptor* CDI que deve ser ativado no arquivo *beans.xml* conforme abaixo:

```xml
<interceptors>
    <class>com.github.ldeitos.tarcius.audit.interceptor.AuditInterceptor</class>
</interceptors>
```

Cabe a observa��o de que *interceptors* do CDI somente s�o acionados quando um m�todo p�blico � invocado por outra classe, mas nunca quando esta chamada � realizada internamente da pr�pria classe que tenha o m�todo anotado. Para maiores informa��es sobre *interceptors* do CDI consulte a [documenta��o oficial](https://docs.oracle.com/javaee/6/tutorial/doc/gkhjx.html).

A anota��o *@Audit* possibilita a especifica��o da refer�ncia do ponto de auditoria atrav�s do atributo *auditRef*. Esta refer�ncia � textual e pode ser utilizada para identificar o ponto de auditoria, mapeando-a para o dom�nio do neg�cio, por exemplo. Quando omitida esta configura��o o nome do m�todo interceptado � utilizado como refer�ncia. 

O processo de auditoria realizada pelo *interceptor* divide-se em quatro fases:

1. Identifica��o dos par�metros a serem auditados
2. Tradu��o dos par�metros
3. Formata��o dos dados coletados
4. Envio dos dados formatados para o reposit�rio de auditoria

#### Identifica��o dos par�metros a serem auditados
Nesta fase s�o identificados todos os par�metros do m�todo que estejam identificados com a anota��o *@Audited* para posterior tradu��o.
A anota��o pode ser aplicada aos par�metros a serem auditados no m�todo marcado para intercepta��o ou diretamente no *bean* que dever� ser auditado, conforme abaixo:

Identificando no par�metro
```java
public class VendasBC {

    @Audit(auditRef="consulta de vendas")
    public void consultar(@Audited(auditRef="par�metros aplicados na consulta") Parametro par){}

}
``` 

Identificando no bean
```java
@Audited(auditRef="par�metros aplicados na consulta") 
public class Parametro {

    ...

}
```
```java
public class VendasBC {

    @Audit(auditRef="consulta de vendas")
    public void consultar(Parametro par){}

}
```

Quando anotado no *bean*, sempre que esse for utilizado como par�metro em um m�todo interceptado ele ser� considerado como parte do conte�do a ser auditado. Entretanto, caso seja necess�rio ignorar em algum caso espec�fico um par�metro cuja classe est� anotada, basta identifica-lo com a anota��o *@NotAudited*, como abaixo:

```java
public class VendasBC {

    @Audit(auditRef="consulta de vendas")
    public void consultar(@NotAudited Parametro par){}

}
```
 
#### Tradu��o dos par�metros
Nesta fase � aplicada a tradu��o dos par�metros anteriormente identificados para *String*. Esta tradu��o pode ser resolvida por tradutores pr�-definidos e disponibilizados pelo componente ou ainda por outros customizados para casos espec�ficos. A configura��o do tradutor a ser aplicado se faz atrav�s do atributo *translator* da anota��o *@Audited*, podendo ter os seguintes valores:

```java
TranslateType.STRING_VALUE
```

Aplica o tradutor que resolve o valor do par�metro auditado para uma *String* representativa do objeto, atrav�s do m�todo String.*valueOf()*. Este tipo � o valor *default* do atributo.

Quando o uso deste tipo de tradutor � associado � configura��o do atributo *format* o resultado obtido da tradu��o do par�metro para *String* � formatado de acordo com a seguinte regra:
 - Quando o par�metro for do tipo *Date* ou extens�es desse a formata��o � realizada pelo *SimpleDateFormat* configurado com o formato definido em *format*;
 - Quando o par�metro for de tipo diferente de *Date*, a formata��o � realizada de acordo com a especifica��o do m�todo String.*format()*;

 A associa��o do atributo *format* com os demais tipos de tradutores **n�o produz nenhum efeito**.
```java
TranslateType.JAXB_XML
```
Aplica o tradutor que resolve o valor do par�metro auditado para o formato XML utilizando o padr�o da [API JAXB]( https://jaxb.java.net/). O XML resultante por este tradutor � apresentado em uma linha �nica, sem formata��o. 
```java
TranslateType.JAXB_FORMATTED_XML
```
Aplica o tradutor que resolve o valor do par�metro auditado para o formato XML utilizando o padr�o da [API JAXB]( https://jaxb.java.net/). Ao contr�rio do anterior, o resultado deste tradutor � o XML formatado.
```java
TranslateType.JAXB_JSON
```
Aplica o tradutor que resolve o valor do par�metro auditado para o formato JSON utilizando o componente [jersey-json]( https://jersey.java.net/documentation/1.19/index.html), extens�o da [API JAXB]( https://jaxb.java.net) que utiliza o mesmo mecanismo de anota��es para determinar o modelo do resultado. Para este formatador o resultado � retornado em uma linha �nica, sem formata��o.
```java
TranslateType.CUSTOM
```
Quando o atributo *translator* est� valorizado com este tipo, significa que a tradu��o do par�metro deve ser resolvida por um tradutor especializado. Quando utilizado este valor, � **obrigat�rio** informar no atributo *customResolverQualifier* com o qualificador *@CustomResolver* que identifica o tradutor a ser instanciado, atrav�s do CDI, para resolver o valor do par�metro. 

As implementa��es especializadas dos tradutores devem implementar a interface *ParameterResolver*, al�m de ser devidamente qualificado com *@CustomResolver*, como no exemplo abaixo:
```java
@ApplicationScope
@CustomResolver("resolve_pedido")
public class PedidoResolver implements ParameterResolver<Pedido> {

    public String resolve(Pedido input){...}

}
```
```java
public class VendasBC {

    @Audit(auditRef="Processar venda")
    public void processar(@Audited(auditRef="pedido", 
                                   translator=TranslateType.CUSTOM, 
                                   customResolverQualifier=@CustomResolver("resolve_pedido")) Pedido pedido){}

}
```
Recomenda-se que as implementa��es de *ParameterResolver* sejam definidas para o escopo de aplica��o, por�m este tratamento n�o � obrigat�rio.

Cabe esclarecer que, apesar de obrigat�rio, a aus�ncia da configura��o do atributo *customResolverQualifier* n�o causa erro no processo de auditoria; neste caso, ser� aplicado o tradutor *default* para resolver o valor auditado, obtendo assim o valor do m�todo *toString()* do objeto.

####Formata��o dos dados coletados
A fase de formata��o dos dados coletados compreende a composi��o dos dados obtidos e traduzidos para texto nas duas fases anteriores dentro do modelo definido pelo usu�rio do componente.

Para o correto funcionamento do componente, � necess�rio que seu usu�rio implemente a interface *AuditDataFormatter<AD>*, aonde **AD** refere-se ao tipo gen�rico do modelo de dados da auditoria, tamb�m definido pelo usu�rio do componente. 

Abaixo segue exemplo de implementa��o do formatador dos dados de auditoria:
```java
public class MeuAuditDataFormatter implements AuditDataFormatter<AuditData> {

	@Override
	public AuditDataContainer<AuditData> format(AuditDataSource preAuditData) {
		AuditData auditData = new AuditData();
		StringBuilder messageBuilder = auditData.getMessage();
		String quebraLinha = System.getProperty("line.separator");

		messageBuilder.append("M�todo auditado: ");
		messageBuilder.append(preAuditData.getAuditReference());

		for (String entrada : preAuditData.getAuditParameterReferences()) {
			messageBuilder.append(quebraLinha).append("Par�metro auditado: ");
			messageBuilder.append(entrada).append(quebraLinha);
			messageBuilder.append("Valor: ").append(preAuditData.getResolvedParameterValues().get(entrada));
		}

		return new AuditDataContainer<AuditData>(auditData);
	}
}
```
O par�metro de entrada do m�todo *AuditDataFormatter.format*, do tipo *AuditDataSource*, cont�m todos os dados coletados durante as fases de identifica��o e tradu��o de dados de auditoria, e podem ser acessados atrav�s dos seguintes m�todos:

 - *getAuditReference()*: Retorna a refer�ncia atribu�da na anota��o *@Audit* atrav�s do atributo *auditRef*, ou, quando este n�o � informado, o nome do m�todo auditado;
 - *getAuditParameterReferences()*: Retorna uma fila (*Queue*) contendo as refer�ncias atribu�das aos par�metros auditados no m�todo, obedecendo a mesma ordem apresentada nesse;
 - *getResolvedParameterValues()*: Retorna um mapa dos valores obtidos atrav�s da aplica��o dos tradutores dos par�metros configurados no atributo *translator* da anota��o *@Audited*, que identifica cada par�metro do m�todo auditado que deve ser considerado na coleta dos dados, conforme j� explicado anteriormente. A chave do mapa � a refer�ncia atribu�da a cada par�metro, citado no item acima;
 - *getParameterValues()*: Retorna um mapa das inst�ncias dos par�metros auditados. A chave do mapa tamb�m � a refer�ncia atribu�da a cada par�metro;

Deve-se observar que o exemplo � apenas representativo, uma vez que tanto o modelo quanto o preenchimento � de decis�o exclusiva do usu�rio do componente.

A partir da vers�o 0.1.2 basta implementar a interface *AuditDataFormatter* que o componente obter� a inst�ncia adequada atrav�s do CDI, entretanto � poss�vel explicitar no arquivo de configura��o *tarcius.xml*, o qual dever� estar localizado no diret�rio META-INF do projeto, o nome qualificado da implementa��o a ser utilizada pelo componente, conforme abaixo:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<tarcius>
	<formatter-class>com.minhaapp.MeuAuditDataFormatter</formatter-class>
	...	
</tarcius>
```
Para as vers�es anteriores esta configura��o � **obrigat�ria**.

####Envio dos dados formatados para o reposit�rio de auditoria
A fase de envio dos dados formatados para o reposit�rio de auditoria compreende a �ltima fase do processo de auditoria. Nesta fase, o usu�rio do componente deve implementar a forma de persist�ncia do modelo formatado na fase anterior, quer seja persistindo em banco de dados, em uma fila de mensageria, no log da aplica��o ou qualquer outro meio que seja definido para o projeto.

Para esta tarefa basta implementar a interface *AuditDataDispatcher<AD>* e nesta implementa��o dar destino ao objeto do tipo gen�rico *AD*, definido pelo usu�rio do componente, que foi formatado no passo anterior, como no exemplo abaixo:
```java
public class MeuAuditDataDispatcher implements AuditDataDispatcher<AuditData> {
	@Inject
	private EntityManager em;

	@Override
	public void dispatch(AuditData auditData) {
		em.merge(auditData);
	}
}
```
A inst�ncia do *AuditDataDispatcher* ser� obtida atrav�s do CDI ou, como citado no t�pico anterior, atrav�s da configura��o do componente, como abaixo:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<tarcius>
	...	
	<dispatcher-class>com.minhaapp.MeuAuditDataDispatcher</dispatcher-class>
	...	
</tarcius>
```
Esta configura��o tamb�m � obrigat�ria para as vers�es anteriores a 0.1.2.