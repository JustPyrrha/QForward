# FabricForwarding
Proxy network support for Fabric modded servers.
                                                                                                                                          
## Proxy Support
Current FabricForwarding supports all proxies that use either legacy forwarding (like BungeeCord) and modern forwarding (like Velocity).
                                                                                                                                          
You can change which IP forwarding method to use in `config/fabric-frowarding.json` by change the `mode` value. Options avaliable are `OFF` for none, `LEGACY` for BungeeCord-style or `MODERN` for modern/Velocity style.
                                                                                                                                          
If you have `"mode":"MODERN"` you **MUST** put the secret key from your proxy in the `secret` value or your proxy wont let anyone connect.