# QForward
Proxy forwarding support for Quilt modded servers.
                                                                                                                                          
## Proxy Support
QForward currently supports Velocity v1 forwarding.
                                                                                                                                          
You can change which IP forwarding method to use in `config/qforward.json` by change the `mode` value. 
Options available are `OFF` for none, `MODERN` for Velocity v1 forwarding.
                                                                                                                                          
If you have `"mode":"MODERN"` you **MUST** put the secret key from your proxy in the `secret` value or your proxy won't let anyone connect.