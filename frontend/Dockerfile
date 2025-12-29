# Build Stage
FROM node:20-alpine AS build

WORKDIR /app

# Accept build arg for API base URL
ARG NG_APP_API_BASE=http://localhost:8080

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Replace API base URL in environment.prod.ts
RUN sed -i "s|apiBase:.*|apiBase: '${NG_APP_API_BASE}'|" src/environments/environment.prod.ts

# Build application
RUN npm run build

# Runtime Stage
FROM nginx:alpine

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from build stage
COPY --from=build /app/dist/confluence-publisher/browser /usr/share/nginx/html

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
