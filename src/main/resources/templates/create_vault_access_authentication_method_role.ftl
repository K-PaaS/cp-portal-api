{
    "audience": "vault",
    "bound_service_account_names": [
        "${db_name}-dynamic-service-account"
    ],
    "bound_service_account_namespaces": [
        "${namespace}"
    ],
    "token_ttl": 0,
    "token_period": 120,
    "token_policies": [
        "${db_name}-policy"
    ]
}