Atividade: Docker Compose

O arquivo compose.yaml utiliza a imagem calculadora-docker:1.0 criada na atividade anterior. A propriedade build também foi mantida para que a imagem possa ser reconstruída no Codespace quando necessário.

O serviço publica a porta 8000, usa uma rede bridge própria e possui restart: unless-stopped. Isso faz o container voltar a iniciar depois de uma falha ou reinicialização, mas respeita uma parada manual.

A calculadora não salva dados e não possui banco, cache ou fila. Por isso, não foi criado volume persistente. O arquivo compose.test.yaml adiciona um serviço temporário com curl para testar a operação 12 / 4 e confirmar o resultado esperado.

Comandos usados no Codespace:

docker compose build
docker compose up -d
docker compose ps
curl -s "http://localhost:8000/?a=12&b=4&op=divide" | grep -o "Resultado:[^<]*"
docker compose -f compose.yaml -f compose.test.yaml run --rm testes
docker compose down
