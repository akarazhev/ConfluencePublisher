# Build stage
FROM node:20-alpine AS build

WORKDIR /app

# Accept build argument for API base URL
ARG NG_APP_API_BASE=http://localhost:8080
ENV NG_APP_API_BASE=$NG_APP_API_BASE

# Copy package files
COPY package.json ./

# Install dependencies
# Note: If package-lock.json exists in the repository, add it to .dockerignore exceptions
# and add a separate COPY package-lock.json ./ line above
RUN npm install

# Copy source code
COPY . .

# Replace apiBase in environment.prod.ts if NG_APP_API_BASE is set
RUN if [ -n "$NG_APP_API_BASE" ]; then \
      sed -i "s|apiBase: '.*'|apiBase: '$NG_APP_API_BASE'|g" src/environments/environment.prod.ts; \
    fi

# Build application
RUN npm run build

# Runtime stage
FROM nginx:alpine

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from dist folder
COPY --from=build /app/dist/confluence-publisher /usr/share/nginx/html

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
