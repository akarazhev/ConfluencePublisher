# Build Stage
FROM node:20-alpine AS build

WORKDIR /app

# Accept build argument for API base URL
ARG NG_APP_API_BASE=http://localhost:8080
ENV NG_APP_API_BASE=$NG_APP_API_BASE

# Copy package files
COPY package.json package-lock.json* ./

# Install dependencies (use npm install if package-lock.json doesn't exist)
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi

# Copy source code
COPY . .

# Replace apiBase in environment.prod.ts with build arg
RUN if [ -f src/environments/environment.prod.ts ]; then \
      sed -i "s|apiBase: '[^']*'|apiBase: '${NG_APP_API_BASE}'|g" src/environments/environment.prod.ts; \
    fi

# Build application
RUN npm run build

# Runtime Stage
FROM nginx:alpine

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from dist folder
COPY --from=build /app/dist/confluence-publisher/browser /usr/share/nginx/html

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80 || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
