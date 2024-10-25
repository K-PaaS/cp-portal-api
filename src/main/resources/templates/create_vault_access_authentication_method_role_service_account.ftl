{
    "audience": "vault",
    "bound_service_account_names": [
        "${app_name}"
    ],
    "bound_service_account_namespaces": [
        "${namespace}"
    ],
    "ttl": "24h",
    "policies": [
        "${db_name}-policy"
    ]
}