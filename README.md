Start console producers and send some records:
```
./kafka-console-producer.sh --bootstrap-server localhost:9092 --property "parse.key=true" --property "key.separator=:" --topic x
1:msg1
...
```
```
./kafka-console-producer.sh --bootstrap-server localhost:9092 --property "parse.key=true" --property "key.separator=:" --topic y
```
```
./kafka-console-producer.sh --bootstrap-server localhost:9092 --property "parse.key=true" --property "key.separator=:" --topic z
```

Observe merged topic with kcat:
```
kcat -b localhost:9092 -t merged
```