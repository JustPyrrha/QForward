# QForward
Proxy forwarding support for Quilt modded servers.
                                                                                                                                          
## Proxy Support
QForward currently supports Velocity v2 forwarding.

## Configuration
There are two config options available in the `config/qforward.json` file.
* `enable_forwarding (boolean, default: false)` - Enable forwarding.
* `forwarding_secret_file (string, default: "forwarding.secret")` - Relative path to a file that contains the forwarding secret.

`forwarding_secret_file` can also be mapped to an environment variable by formatting it as `env:ENVIRONMENT_VARIABLE` (eg `env:QFORWARD_SECRET`)