/*jshint -W069 */
/**
 * Contains swagger
 * @class Test
 * @param {(string|object)} [domainOrOptions] - The project domain or options object. If object, see the object's optional properties.
 * @param {string} [domainOrOptions.domain] - The project domain
 * @param {object} [domainOrOptions.token] - auth token - object with value property and optional headerOrQueryName and isQuery properties
 */
var Test = (function() {
    'use strict';

    var request = require('request');
    var Q = require('q');

    function Test(options) {
        var domain = (typeof options === 'object') ? options.domain : options;
        this.domain = domain ? domain : '';
        if (this.domain.length === 0) {
            throw new Error('Domain parameter must be specified as a string.');
        }
    }

    function mergeQueryParams(parameters, queryParameters) {
        if (parameters.$queryParameters) {
            Object.keys(parameters.$queryParameters)
                .forEach(function(parameterName) {
                    var parameter = parameters.$queryParameters[parameterName];
                    queryParameters[parameterName] = parameter;
                });
        }
        return queryParameters;
    }

    /**
     * HTTP Request
     * @method
     * @name Test#request
     * @param {string} method - http method
     * @param {string} url - url to do request
     * @param {object} parameters
     * @param {object} body - body parameters / object
     * @param {object} headers - header parameters
     * @param {object} queryParameters - querystring parameters
     * @param {object} form - form data object
     * @param {object} deferred - promise object
     */
    Test.prototype.request = function(method, url, parameters, body, headers, queryParameters, form, deferred) {
        var req = {
            method: method,
            uri: url,
            qs: queryParameters,
            headers: headers,
            body: body
        };
        if (Object.keys(form).length > 0) {
            req.form = form;
        }
        if (typeof(body) === 'object' && !(body instanceof Buffer)) {
            req.json = true;
        }
        request(req, function(error, response, body) {
            if (error) {
                deferred.reject(error);
            } else {
                if (/^application\/(.*\\+)?json/.test(response.headers['content-type'])) {
                    try {
                        body = JSON.parse(body);
                    } catch (e) {}
                }
                if (response.statusCode === 204) {
                    deferred.resolve({
                        response: response
                    });
                } else if (response.statusCode >= 200 && response.statusCode <= 299) {
                    deferred.resolve({
                        response: response,
                        body: body
                    });
                } else {
                    deferred.reject({
                        response: response,
                        body: body
                    });
                }
            }
        });
    };

    /**
     * Legge il config di un app
     * @method
     * @name Test#getAppConfigUsingGET
     * @param {object} parameters - method options and parameters
     * @param {string} parameters.appName - appName
     */
    Test.prototype.getAppConfigUsingGET = function(parameters) {
        if (parameters === undefined) {
            parameters = {};
        }
        var deferred = Q.defer();
        var domain = this.domain,
            path = '/api/v1/app/config/{appName}';
        var body = {},
            queryParameters = {},
            headers = {},
            form = {};

        headers['Accept'] = ['*/*'];

        path = path.replace('{appName}', parameters['appName']);

        if (parameters['appName'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: appName'));
            return deferred.promise;
        }

        queryParameters = mergeQueryParams(parameters, queryParameters);

        this.request('GET', domain + path, parameters, body, headers, queryParameters, form, deferred);

        return deferred.promise;
    };
    /**
     * Scrive il config di un app
     * @method
     * @name Test#setAppConfigUsingPUT
     * @param {object} parameters - method options and parameters
     * @param {string} parameters.appName - appName
     * @param {} parameters.info - info
     */
    Test.prototype.setAppConfigUsingPUT = function(parameters) {
        if (parameters === undefined) {
            parameters = {};
        }
        var deferred = Q.defer();
        var domain = this.domain,
            path = '/api/v1/app/config/{appName}';
        var body = {},
            queryParameters = {},
            headers = {},
            form = {};

        headers['Accept'] = ['*/*'];
        headers['Content-Type'] = ['application/json'];

        path = path.replace('{appName}', parameters['appName']);

        if (parameters['appName'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: appName'));
            return deferred.promise;
        }

        if (parameters['info'] !== undefined) {
            body = parameters['info'];
        }

        if (parameters['info'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: info'));
            return deferred.promise;
        }

        queryParameters = mergeQueryParams(parameters, queryParameters);

        this.request('PUT', domain + path, parameters, body, headers, queryParameters, form, deferred);

        return deferred.promise;
    };
    /**
     * Legge il config di un servizio
     * @method
     * @name Test#getServiceConfigUsingGET
     * @param {object} parameters - method options and parameters
     * @param {string} parameters.serviceName - serviceName
     */
    Test.prototype.getServiceConfigUsingGET = function(parameters) {
        if (parameters === undefined) {
            parameters = {};
        }
        var deferred = Q.defer();
        var domain = this.domain,
            path = '/api/v1/service/config/{serviceName}';
        var body = {},
            queryParameters = {},
            headers = {},
            form = {};

        headers['Accept'] = ['application/json'];

        path = path.replace('{serviceName}', parameters['serviceName']);

        if (parameters['serviceName'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: serviceName'));
            return deferred.promise;
        }

        queryParameters = mergeQueryParams(parameters, queryParameters);

        this.request('GET', domain + path, parameters, body, headers, queryParameters, form, deferred);

        return deferred.promise;
    };
    /**
     * Scrive il config di un servizio
     * @method
     * @name Test#setServiceConfigUsingPUT
     * @param {object} parameters - method options and parameters
     * @param {string} parameters.serviceName - serviceName
     * @param {} parameters.info - info
     */
    Test.prototype.setServiceConfigUsingPUT = function(parameters) {
        if (parameters === undefined) {
            parameters = {};
        }
        var deferred = Q.defer();
        var domain = this.domain,
            path = '/api/v1/service/config/{serviceName}';
        var body = {},
            queryParameters = {},
            headers = {},
            form = {};

        headers['Accept'] = ['*/*'];
        headers['Content-Type'] = ['application/json'];

        path = path.replace('{serviceName}', parameters['serviceName']);

        if (parameters['serviceName'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: serviceName'));
            return deferred.promise;
        }

        if (parameters['info'] !== undefined) {
            body = parameters['info'];
        }

        if (parameters['info'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: info'));
            return deferred.promise;
        }

        queryParameters = mergeQueryParams(parameters, queryParameters);

        this.request('PUT', domain + path, parameters, body, headers, queryParameters, form, deferred);

        return deferred.promise;
    };
    /**
     * Salva l'endpoint, con expire
     * @method
     * @name Test#postEndpointUsingPOST
     * @param {object} parameters - method options and parameters
     * @param {} parameters.info - info
     */
    Test.prototype.postEndpointUsingPOST = function(parameters) {
        if (parameters === undefined) {
            parameters = {};
        }
        var deferred = Q.defer();
        var domain = this.domain,
            path = '/api/v1/service/endpoint';
        var body = {},
            queryParameters = {},
            headers = {},
            form = {};

        headers['Accept'] = ['*/*'];
        headers['Content-Type'] = ['application/json'];

        if (parameters['info'] !== undefined) {
            body = parameters['info'];
        }

        if (parameters['info'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: info'));
            return deferred.promise;
        }

        queryParameters = mergeQueryParams(parameters, queryParameters);

        this.request('POST', domain + path, parameters, body, headers, queryParameters, form, deferred);

        return deferred.promise;
    };
    /**
     * ritorna l'elenco di tutti gli endpoint attivi
     * @method
     * @name Test#getAllEndpointUsingGET
     * @param {object} parameters - method options and parameters
     */
    Test.prototype.getAllEndpointUsingGET = function(parameters) {
        if (parameters === undefined) {
            parameters = {};
        }
        var deferred = Q.defer();
        var domain = this.domain,
            path = '/api/v1/service/endpoint/all';
        var body = {},
            queryParameters = {},
            headers = {},
            form = {};

        headers['Accept'] = ['*/*'];

        queryParameters = mergeQueryParams(parameters, queryParameters);

        this.request('GET', domain + path, parameters, body, headers, queryParameters, form, deferred);

        return deferred.promise;
    };
    /**
     * dato un serviceName ritorna l'elenco degli URL degli endpoint attivi
     * @method
     * @name Test#getEndpointUsingGET
     * @param {object} parameters - method options and parameters
     * @param {string} parameters.serviceName - serviceName
     */
    Test.prototype.getEndpointUsingGET = function(parameters) {
        if (parameters === undefined) {
            parameters = {};
        }
        var deferred = Q.defer();
        var domain = this.domain,
            path = '/api/v1/service/endpoint/{serviceName}';
        var body = {},
            queryParameters = {},
            headers = {},
            form = {};

        headers['Accept'] = ['*/*'];

        path = path.replace('{serviceName}', parameters['serviceName']);

        if (parameters['serviceName'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: serviceName'));
            return deferred.promise;
        }

        queryParameters = mergeQueryParams(parameters, queryParameters);

        this.request('GET', domain + path, parameters, body, headers, queryParameters, form, deferred);

        return deferred.promise;
    };
    /**
     * Legge l'info di un servizio
     * @method
     * @name Test#getServiceInfoUsingGET
     * @param {object} parameters - method options and parameters
     * @param {string} parameters.serviceName - serviceName
     */
    Test.prototype.getServiceInfoUsingGET = function(parameters) {
        if (parameters === undefined) {
            parameters = {};
        }
        var deferred = Q.defer();
        var domain = this.domain,
            path = '/api/v1/service/info/{serviceName}';
        var body = {},
            queryParameters = {},
            headers = {},
            form = {};

        headers['Accept'] = ['*/*'];

        path = path.replace('{serviceName}', parameters['serviceName']);

        if (parameters['serviceName'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: serviceName'));
            return deferred.promise;
        }

        queryParameters = mergeQueryParams(parameters, queryParameters);

        this.request('GET', domain + path, parameters, body, headers, queryParameters, form, deferred);

        return deferred.promise;
    };
    /**
     * Scrive l'info di un servizio
     * @method
     * @name Test#setServiceInfoUsingPUT
     * @param {object} parameters - method options and parameters
     * @param {string} parameters.serviceName - serviceName
     * @param {} parameters.info - info
     */
    Test.prototype.setServiceInfoUsingPUT = function(parameters) {
        if (parameters === undefined) {
            parameters = {};
        }
        var deferred = Q.defer();
        var domain = this.domain,
            path = '/api/v1/service/info/{serviceName}';
        var body = {},
            queryParameters = {},
            headers = {},
            form = {};

        headers['Accept'] = ['*/*'];
        headers['Content-Type'] = ['application/json'];

        path = path.replace('{serviceName}', parameters['serviceName']);

        if (parameters['serviceName'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: serviceName'));
            return deferred.promise;
        }

        if (parameters['info'] !== undefined) {
            body = parameters['info'];
        }

        if (parameters['info'] === undefined) {
            deferred.reject(new Error('Missing required  parameter: info'));
            return deferred.promise;
        }

        queryParameters = mergeQueryParams(parameters, queryParameters);

        this.request('PUT', domain + path, parameters, body, headers, queryParameters, form, deferred);

        return deferred.promise;
    };

    return Test;
})();

exports.Test = Test;