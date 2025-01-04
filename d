services:
  db:
    container_name: postgres
    image: postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: ecommerce
      PGDATA: /data/postgres
    volumes:
      - db:/data/postgres
    ports:
      - "5433:5432"
    networks:
      - db
    restart: unless-stopped
networks:
  db:
    driver: bridge
volumes:
  db:

  services:
    db:
      container_name: postgres
      image: postgres
      environment:
        POSTGRES_USER: mimche
        POSTGRES_PASSWORD: miEykIRS90pK5kMTVHTNVUXx9VGfiFsP
        POSTGRES_DB: ecommerce_xf71
        PGDATA: /data/postgres
      volumes:
        - db:/data/postgres
      ports:
        - "5433:5432"
      networks:
        - db
      restart: unless-stopped
  networks:
    db:
      driver: bridge
  volumes:
    db: