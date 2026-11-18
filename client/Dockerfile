# Build the React UI and serve the static files.
# The API URL is baked into the bundle and must be reachable from the browser.
FROM node:18-bookworm AS build
WORKDIR /workspace

COPY package.json package-lock.json ./
RUN npm ci --ignore-scripts --legacy-peer-deps \
    && npm install --no-save --ignore-scripts --legacy-peer-deps \
        @babel/core@7.26.10 \
        @babel/preset-env@7.26.9 \
        @babel/preset-react@7.26.3 \
        @babel/preset-typescript@7.27.1 \
        babel-loader@9.2.1 \
        style-loader@3.3.4 \
        css-loader@6.11.0 \
        less@4.2.2 \
        less-loader@12.2.0 \
        webpack-cli@5.1.4

COPY . .
ARG API_SERVER_URL=http://localhost:9966/petclinic
ENV API_SERVER_URL=${API_SERVER_URL}
RUN npx webpack --config webpack.config.docker.js

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/public /usr/share/nginx/html
EXPOSE 80
