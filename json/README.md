# Generate serializer and deserializer method that will not uses reflection

### annotate class with

```java
    @com.lucas.json.serialize.Serializer
```
### if you whant generate toJson method

### annotate class with

```java
    @com.lucas.json.serialize.Deserializer
```
### if you whant generate toObject method

### for example:

```java
    @Deserializer
    @Serializer
    public class ResponsePayment extends Deserialize implements JsonSerializable {
        private Header head;
        private ResponseBodyPayment body;
    }
```

### add json as dependency 

```xml
		<dependency>
			<groupId>io.github.lucacirillo2738</groupId>
			<artifactId>json</artifactId>
        </dependency>
```

### add json as plugin and specify sourceDir
```xml
    <plugin>
        <groupId>io.github.lucacirillo2738</groupId>
        <artifactId>json</artifactId>
        <version>1.0.3</version>
        <executions>
            <execution>
                <id>serialize</id>
                <phase>compile</phase>
                <goals>
                    <goal>serialize</goal>
                </goals>
            </execution>
            <execution>
                <id>deserialize</id>
                <phase>compile</phase>
                <goals>
                    <goal>deserialize</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <sourceDir>com.lucas</sourceDir>
        </configuration>
    </plugin>
```

### it will generate in the example of ResponsePayment the folllowing class

```java
    @Deserializer
    @Serializer
    public class ResponsePayment extends Deserialize implements JsonSerializable {
    
        private Header head;
        private ResponseBodyPayment body;
    
        public static ResponsePayment toObject(JsonParser var0) throws Exception {
            var0.nextToken();
            if (var0.getCurrentName() != null) {
                String.valueOf(var0.getCurrentName()).toLowerCase();
            } else {
                Object var10000 = null;
            }
    
            ResponsePayment var2 = new ResponsePayment();
    
            try {
                while (var0.nextToken() != JsonToken.END_OBJECT && var0.getCurrentToken() != null) {
                    String var1 = var0.getCurrentName() != null ? String.valueOf(var0.getCurrentName()).toLowerCase() : null;
                    if ("head".equals(var1)) {
                        var0.nextToken();
                        Header var3 = new Header();
    
                        try {
                            while (var0.nextToken() != JsonToken.END_OBJECT && var0.getCurrentToken() != null) {
                                var1 = var0.getCurrentName() != null ? String.valueOf(var0.getCurrentName()).toLowerCase() : null;
                                if ("reg".equals(var1)) {
                                    var0.nextToken();
                                    var3.setReg(var0.getText());
                                } else if ("resc".equals(var1)) {
                                    var0.nextToken();
                                    var3.setResc(var0.getIntValue());
                                } else if ("msgcode".equals(var1)) {
                                    var0.nextToken();
                                    var3.setMsgcode(var0.getIntValue());
                                } else if ("code".equals(var1)) {
                                    var0.nextToken();
                                    var3.setCode(var0.getText());
                                } else if ("idterm".equals(var1)) {
                                    var0.nextToken();
                                    var3.setIdterm(var0.getIntValue());
                                } else if ("gamecode".equals(var1)) {
                                    var0.nextToken();
                                    var3.setGamecode(var0.getIntValue());
                                } else if ("msn".equals(var1)) {
                                    var0.nextToken();
                                    var3.setMsn(var0.getIntValue());
                                }
                            }
                        } catch (JsonParseException var16) {
                            throw new ParsingException("the provided Json to parse has a different structure than destination Class", var16);
                        }
    
                        var2.setHead(var3);
                    } else if ("body".equals(var1)) {
                        var0.nextToken();
                        ResponseBodyPayment var5 = new ResponseBodyPayment();
    
                        try {
                            while (var0.nextToken() != JsonToken.END_OBJECT && var0.getCurrentToken() != null) {
                                var1 = var0.getCurrentName() != null ? String.valueOf(var0.getCurrentName()).toLowerCase() : null;
                                if ("term".equals(var1)) {
                                    var0.nextToken();
                                    TerminalType var6 = new TerminalType();
    
                                    try {
                                        while (var0.nextToken() != JsonToken.END_OBJECT && var0.getCurrentToken() != null) {
                                            var1 = var0.getCurrentName() != null ? String.valueOf(var0.getCurrentName()).toLowerCase() : null;
                                            if ("reg".equals(var1)) {
                                                var0.nextToken();
                                                var6.setReg(var0.getText());
                                            } else if ("resc".equals(var1)) {
                                                var0.nextToken();
                                                var6.setResc(var0.getText());
                                            } else if ("code".equals(var1)) {
                                                var0.nextToken();
                                                var6.setCode(var0.getText());
                                            } else if ("id".equals(var1)) {
                                                var0.nextToken();
                                                var6.setId(new Integer(var0.getIntValue()));
                                            } else if ("regc".equals(var1)) {
                                                var0.nextToken();
                                                var6.setRegc(var0.getIntValue());
                                            }
                                        }
                                    } catch (JsonParseException var17) {
                                        throw new ParsingException("the provided Json to parse has a different structure than destination Class", var17);
                                    }
    
                                    var5.setTerm(var6);
                                } else if ("ticketid".equals(var1)) {
                                    var0.nextToken();
                                    TicketIdType var8 = new TicketIdType();
    
                                    try {
                                        while (var0.nextToken() != JsonToken.END_OBJECT && var0.getCurrentToken() != null) {
                                            var1 = var0.getCurrentName() != null ? String.valueOf(var0.getCurrentName()).toLowerCase() : null;
                                            if ("sn".equals(var1)) {
                                                var0.nextToken();
                                                var8.setSn(var0.getText());
                                            } else if ("prsn".equals(var1)) {
                                                var0.nextToken();
                                                var8.setPrSn(var0.getText());
                                            } else if ("event".equals(var1)) {
                                                var0.nextToken();
                                                EventIdType var9 = new EventIdType();
    
                                                try {
                                                    while (var0.nextToken() != JsonToken.END_OBJECT && var0.getCurrentToken() != null) {
                                                        var1 = var0.getCurrentName() != null ? String.valueOf(var0.getCurrentName()).toLowerCase() : null;
                                                        if ("year".equals(var1)) {
                                                            var0.nextToken();
                                                            var9.setYear(var0.getIntValue());
                                                        } else if ("num".equals(var1)) {
                                                            var0.nextToken();
                                                            var9.setNum(var0.getIntValue());
                                                        } else if ("gamecode".equals(var1)) {
                                                            var0.nextToken();
                                                            var9.setGameCode(var0.getValueAsString());
                                                        }
                                                    }
                                                } catch (JsonParseException var18) {
                                                    throw new ParsingException("the provided Json to parse has a different structure than destination Class", var18);
                                                }
    
                                                var8.setEvent(var9);
                                            }
                                        }
                                    } catch (JsonParseException var19) {
                                        throw new ParsingException("the provided Json to parse has a different structure than destination Class", var19);
                                    }
    
                                    var5.setTicketId(var8);
                                } else if ("result".equals(var1)) {
                                    var0.nextToken();
                                    ResultType var12 = new ResultType();
    
                                    try {
                                        while (var0.nextToken() != JsonToken.END_OBJECT && var0.getCurrentToken() != null) {
                                            var1 = var0.getCurrentName() != null ? String.valueOf(var0.getCurrentName()).toLowerCase() : null;
                                            if ("descr".equals(var1)) {
                                                var0.nextToken();
                                                var12.setDescr(var0.getText());
                                            } else if ("code".equals(var1)) {
                                                var0.nextToken();
                                                var12.setCode(var0.getText());
                                            }
                                        }
                                    } catch (JsonParseException var20) {
                                        throw new ParsingException("the provided Json to parse has a different structure than destination Class", var20);
                                    }
    
                                    var5.setResult(var12);
                                }
                            }
                        } catch (JsonParseException var21) {
                            throw new ParsingException("the provided Json to parse has a different structure than destination Class", var21);
                        }
    
                        var2.setBody(var5);
                    }
                }
    
                return var2;
            } catch (JsonParseException var22) {
                throw new ParsingException("the provided Json to parse has a different structure than destination Class", var22);
            }
        }
    
        public static ResponsePayment toObject(JsonFactory var0, String var1) throws Exception {
            var1 = var1.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "");
            JsonParser var2 = var0.createParser(var1);
            return toObject(var2);
        }
    
        public static ResponsePayment toObject(String var0) throws Exception {
            var0 = var0.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "");
            JsonFactory var1 = new JsonFactory();
            var1.createParser(var0);
            return toObject(var1, var0);
        }
    
        public Header getHead() {
            return head;
        }
    
        public void setHead(Header head) {
            this.head = head;
        }
    
        public ResponseBodyPayment getBody() {
            return body;
        }
    
        public void setBody(ResponseBodyPayment body) {
            this.body = body;
        }
    
        public String toJson() {
            StringBuilder var1 = new StringBuilder();
            var1.append("{");
            var1.append("\"body\":");
            if (this.getBody() != null) {
                var1.append("{");
                var1.append("\"term\":");
                if (this.getBody().getTerm() != null) {
                    var1.append("{");
                    var1.append("\"reg\":");
                    if (this.getBody().getTerm().getReg() != null) {
                        var1.append("\"" + this.getBody().getTerm().getReg() + "\",");
                    } else {
                        var1.append("null");
                    }
    
                    var1.append(",");
                    var1.append("\"resc\":");
                    if (this.getBody().getTerm().getResc() != null) {
                        var1.append("\"" + this.getBody().getTerm().getResc() + "\",");
                    } else {
                        var1.append("null");
                    }
    
                    var1.append(",");
                    var1.append("\"code\":");
                    if (this.getBody().getTerm().getCode() != null) {
                        var1.append("\"" + this.getBody().getTerm().getCode() + "\",");
                    } else {
                        var1.append("null");
                    }
    
                    var1.append(",");
                    var1.append("\"id\":");
                    var1.append(this.getBody().getTerm().getId());
                    var1.append(",");
                    var1.append("\"regc\":");
                    var1.append(this.getBody().getTerm().getRegc());
                    var1.append("}");
                    if (!"((it.sisal.pgd.vincicasa.model.payment.ResponseBodyPayment)((it.sisal.pgd.vincicasa.model.payment.ResponsePayment)this).getBody()).getTerm()".equals("this")) {
                        var1.append(",");
                    }
                } else {
                    var1.append("null");
                }
    
                var1.append(",");
                var1.append("\"ticketId\":");
                if (this.getBody().getTicketId() != null) {
                    var1.append("{");
                    var1.append("\"sn\":");
                    if (this.getBody().getTicketId().getSn() != null) {
                        var1.append("\"" + this.getBody().getTicketId().getSn() + "\",");
                    } else {
                        var1.append("null");
                    }
    
                    var1.append(",");
                    var1.append("\"prSn\":");
                    if (this.getBody().getTicketId().getPrSn() != null) {
                        var1.append("\"" + this.getBody().getTicketId().getPrSn() + "\",");
                    } else {
                        var1.append("null");
                    }
    
                    var1.append(",");
                    var1.append("\"event\":");
                    if (this.getBody().getTicketId().getEvent() != null) {
                        var1.append("{");
                        var1.append("\"year\":");
                        var1.append(this.getBody().getTicketId().getEvent().getYear());
                        var1.append(",");
                        var1.append("\"num\":");
                        var1.append(this.getBody().getTicketId().getEvent().getNum());
                        var1.append(",");
                        var1.append("\"gameCode\":");
                        if (this.getBody().getTicketId().getEvent().getGameCode() != null) {
                            var1.append("\"" + this.getBody().getTicketId().getEvent().getGameCode() + "\",");
                        } else {
                            var1.append("null");
                        }
    
                        var1.append("}");
                        if (!"((it.sisal.pgd.vincicasa.model.TicketIdType)((it.sisal.pgd.vincicasa.model.payment.ResponseBodyPayment)((it.sisal.pgd.vincicasa.model.payment.ResponsePayment)this).getBody()).getTicketId()).getEvent()".equals("this")) {
                            var1.append(",");
                        }
                    } else {
                        var1.append("null");
                    }
    
                    var1.append("}");
                    if (!"((it.sisal.pgd.vincicasa.model.payment.ResponseBodyPayment)((it.sisal.pgd.vincicasa.model.payment.ResponsePayment)this).getBody()).getTicketId()".equals("this")) {
                        var1.append(",");
                    }
                } else {
                    var1.append("null");
                }
    
                var1.append(",");
                var1.append("\"result\":");
                if (this.getBody().getResult() != null) {
                    var1.append("{");
                    var1.append("\"descr\":");
                    if (this.getBody().getResult().getDescr() != null) {
                        var1.append("\"" + this.getBody().getResult().getDescr() + "\",");
                    } else {
                        var1.append("null");
                    }
    
                    var1.append(",");
                    var1.append("\"code\":");
                    if (this.getBody().getResult().getCode() != null) {
                        var1.append("\"" + this.getBody().getResult().getCode() + "\",");
                    } else {
                        var1.append("null");
                    }
    
                    var1.append("}");
                    if (!"((it.sisal.pgd.vincicasa.model.payment.ResponseBodyPayment)((it.sisal.pgd.vincicasa.model.payment.ResponsePayment)this).getBody()).getResult()".equals("this")) {
                        var1.append(",");
                    }
                } else {
                    var1.append("null");
                }
    
                var1.append("}");
                if (!"((it.sisal.pgd.vincicasa.model.payment.ResponsePayment)this).getBody()".equals("this")) {
                    var1.append(",");
                }
            } else {
                var1.append("null");
            }
    
            var1.append(",");
            var1.append("\"head\":");
            if (this.getHead() != null) {
                var1.append("{");
                var1.append("\"reg\":");
                if (this.getHead().getReg() != null) {
                    var1.append("\"" + this.getHead().getReg() + "\",");
                } else {
                    var1.append("null");
                }
    
                var1.append(",");
                var1.append("\"resc\":");
                var1.append(this.getHead().getResc());
                var1.append(",");
                var1.append("\"msgcode\":");
                var1.append(this.getHead().getMsgcode());
                var1.append(",");
                var1.append("\"code\":");
                if (this.getHead().getCode() != null) {
                    var1.append("\"" + this.getHead().getCode() + "\",");
                } else {
                    var1.append("null");
                }
    
                var1.append(",");
                var1.append("\"idterm\":");
                var1.append(this.getHead().getIdterm());
                var1.append(",");
                var1.append("\"gamecode\":");
                var1.append(this.getHead().getGamecode());
                var1.append(",");
                var1.append("\"msn\":");
                var1.append(this.getHead().getMsn());
                var1.append(",");
                var1.append("\"regc\":");
                var1.append(this.getHead().getRegc());
                var1.append("}");
                if (!"((it.sisal.pgd.vincicasa.model.payment.ResponsePayment)this).getHead()".equals("this")) {
                    var1.append(",");
                }
            } else {
                var1.append("null");
            }
    
            var1.append("}");
            if (!"this".equals("this")) {
                var1.append(",");
            }
    
            return var1.toString().replaceAll(",,", ",").replaceAll(",}", "}").replaceAll(",]", "]");
        }
    }
```

### now you can youse response.toJson() method and ResponsePayment.toObject() methods
