package com.nutrizulia.util

object AppointmentConstants {
    @Deprecated("Use UserPreferencesRepository to get dynamic max appointments per day")
    const val MAX_APPOINTMENTS_PER_DAY: Int = 1
    
    // Default value is now managed by UserPreferencesRepository
    // Use GetMaxAppointmentsPerDayUseCase to get the current user preference
}

object ApiConstants {
    const val BASE_URL: String = "http://192.168.1.100:8080/"
}

// ========== AUTHENTICATION API ==========
object AuthEndpoints {
    private const val AUTH_BASE_URL: String = "/api/v1/auth"
    
    const val SIGN_IN: String = "$AUTH_BASE_URL/sign-in"
    const val SIGN_UP: String = "$AUTH_BASE_URL/sign-up"
    const val CHECK_AUTH: String = "$AUTH_BASE_URL/check"
    const val FORGOT_PASSWORD: String = "$AUTH_BASE_URL/forgot-password"
    const val CHANGE_PASSWORD: String = "$AUTH_BASE_URL/change-password"
}

// ========== CATALOG API ==========
object CatalogEndpoints {
    private const val CATALOG_BASE_URL: String = "/api/v1/catalog"
    
    const val DISEASES: String = "$CATALOG_BASE_URL/diseases"
    const val SPECIALTIES: String = "$CATALOG_BASE_URL/specialties"
    const val STATES: String = "$CATALOG_BASE_URL/states"
    const val ETHNICITIES: String = "$CATALOG_BASE_URL/ethnicities"
    const val AGE_GROUPS: String = "$CATALOG_BASE_URL/age-groups"
    const val MUNICIPALITIES: String = "$CATALOG_BASE_URL/municipalities"
    const val HEALTH_MUNICIPALITIES: String = "$CATALOG_BASE_URL/health-municipalities"
    const val NATIONALITIES: String = "$CATALOG_BASE_URL/nationalities"
    const val CHILDREN_AGE_PARAMETERS: String = "$CATALOG_BASE_URL/children-age-parameters"
    const val PEDIATRIC_AGE_PARAMETERS: String = "$CATALOG_BASE_URL/pediatric-age-parameters"
    const val PEDIATRIC_LENGTH_PARAMETERS: String = "$CATALOG_BASE_URL/pediatric-length-parameters"
    const val RELATIONSHIPS: String = "$CATALOG_BASE_URL/relationships"
    const val PARISHES: String = "$CATALOG_BASE_URL/parishes"
    const val REGEX: String = "$CATALOG_BASE_URL/regex"
    const val BMI_INTERPRETATION_RULES: String = "$CATALOG_BASE_URL/bmi-interpretation-rules"
    const val PERCENTILE_INTERPRETATION_RULES: String = "$CATALOG_BASE_URL/percentile-interpretation-rules"
    const val Z_SCORE_INTERPRETATION_RULES: String = "$CATALOG_BASE_URL/z-score-interpretation-rules"
    const val BIOLOGICAL_RISKS: String = "$CATALOG_BASE_URL/biological-risks"
    const val ROLES: String = "$CATALOG_BASE_URL/roles"
    const val ACTIVITY_TYPES: String = "$CATALOG_BASE_URL/activity-types"
    const val INDICATOR_TYPES: String = "$CATALOG_BASE_URL/indicator-types"
    const val INSTITUTION_TYPES: String = "$CATALOG_BASE_URL/institution-types"
    const val VERSIONS: String = "$CATALOG_BASE_URL/versions"
}

// ========== DATA COLLECTION API ==========
object CollectionSyncEndpoints {
    private const val COLLECTION_BASE_URL: String = "/api/v1/collection"
    
    const val SYNC_PATIENTS: String = "$COLLECTION_BASE_URL/sync/patients"
    const val SYNC_REPRESENTATIVES: String = "$COLLECTION_BASE_URL/sync/representatives"
    const val SYNC_PATIENT_REPRESENTATIVES: String = "$COLLECTION_BASE_URL/sync/patient-representatives"
    const val SYNC_CONSULTATIONS: String = "$COLLECTION_BASE_URL/sync/consultations"
    const val SYNC_DIAGNOSES: String = "$COLLECTION_BASE_URL/sync/diagnoses"
    const val SYNC_ANTHROPOMETRIC_EVALUATIONS: String = "$COLLECTION_BASE_URL/sync/anthropometric-evaluations"
    const val SYNC_ANTHROPOMETRIC_DETAILS: String = "$COLLECTION_BASE_URL/sync/anthropometric-details"
    const val SYNC_VITAL_DETAILS: String = "$COLLECTION_BASE_URL/sync/vital-details"
    const val SYNC_METABOLIC_DETAILS: String = "$COLLECTION_BASE_URL/sync/metabolic-details"
    const val SYNC_PEDIATRIC_DETAILS: String = "$COLLECTION_BASE_URL/sync/pediatric-details"
    const val SYNC_OBSTETRIC_DETAILS: String = "$COLLECTION_BASE_URL/sync/obstetric-details"
    const val SYNC_ACTIVITIES: String = "$COLLECTION_BASE_URL/sync/activities"
}

// ========== INSTITUTIONS API ==========
object InstitutionEndpoints {
    private const val INSTITUTIONS_BASE_URL: String = "/api/v1/institutions"
    
    const val GET_ALL: String = INSTITUTIONS_BASE_URL
}

// ========== USERS API ==========
object UserEndpoints {
    private const val USERS_BASE_URL: String = "/api/v1/users"
    
    const val GET_ALL: String = USERS_BASE_URL
}

// ========== USER-INSTITUTIONS API ==========
object UserInstitutionEndpoints {
    private const val USER_INSTITUTIONS_BASE_URL: String = "/api/v1/user-institutions"
    
    const val GET_BY_USER: String = "$USER_INSTITUTIONS_BASE_URL/by-user"
}