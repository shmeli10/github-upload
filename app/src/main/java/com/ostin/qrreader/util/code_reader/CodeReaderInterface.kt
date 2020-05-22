package com.ostin.qrreader.util.code_reader

interface CodeReaderInterface {
    fun onCodeValueDetected(codeValue: String)
    fun onCodeImgPathDetected(codeImgPath: String)
}