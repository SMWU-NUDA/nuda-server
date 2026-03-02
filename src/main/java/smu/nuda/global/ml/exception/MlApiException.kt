package smu.nuda.global.ml.exception

import smu.nuda.global.error.DomainException
import smu.nuda.global.error.ErrorCode

class MlApiException(errorCode: ErrorCode) : DomainException(errorCode)