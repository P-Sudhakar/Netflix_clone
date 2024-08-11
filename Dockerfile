FROM httpd
WORKDIR /usr/local/apache2/htdocs
RUN rm -rf /usr/local/apache2/htdocs/index.html
COPY . .
EXPOSE 80
