FROM httpd:2.4

# Set working directory
WORKDIR /usr/local/apache2/htdocs

# Remove default index file
RUN rm -rf index.html

# Copy application files
COPY . .

# Expose port 80
EXPOSE 80

# Create and use a non-root user
RUN useradd -m apacheuser
USER apacheuser

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:80 || exit 1
