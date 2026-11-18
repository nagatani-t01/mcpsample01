const path = require('path');
const webpack = require('webpack');
const config = require('./webpack.config.docker');

const apiServerUrl = process.env.API_SERVER_URL || 'http://localhost:9966/petclinic';

config.mode = 'development';

// Replace the production DefinePlugin so NODE_ENV is not set to two different values.
config.plugins = config.plugins.filter(plugin => plugin.constructor.name !== 'DefinePlugin');
config.plugins.push(new webpack.DefinePlugin({
  'process.env.NODE_ENV': JSON.stringify('development'),
  __API_SERVER_URL__: JSON.stringify(apiServerUrl)
}));

config.watchOptions = {
  poll: 1000,
  aggregateTimeout: 300
};
config.devServer = {
  host: '0.0.0.0',
  port: 4444,
  hot: true,
  allowedHosts: 'all',
  historyApiFallback: true,
  static: {
    directory: path.join(__dirname, 'public')
  },
  devMiddleware: {
    publicPath: '/dist/'
  }
};

module.exports = config;
