'use strict'

const Module = require('node:module')

const originalResolveFilename = Module._resolveFilename
const compatLruCachePath = require.resolve('lru-cache')

Module._resolveFilename = function patchedResolveFilename(
  request,
  parent,
  isMain,
  options
) {
  if (
    request === 'lru-cache' &&
    typeof parent?.filename === 'string' &&
    parent.filename.includes('miniprogram-ci')
  ) {
    return compatLruCachePath
  }

  return originalResolveFilename.call(this, request, parent, isMain, options)
}
