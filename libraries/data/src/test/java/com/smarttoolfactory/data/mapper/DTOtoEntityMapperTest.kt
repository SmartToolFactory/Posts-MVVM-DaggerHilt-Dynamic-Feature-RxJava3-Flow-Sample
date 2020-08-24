package com.smarttoolfactory.data.mapper

import com.google.common.truth.Truth
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import org.junit.jupiter.api.Test

class DTOtoEntityMapperTest {

    private val postDTOList by lazy {
        convertFromJsonToListOf<PostDTO>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    private val postEntityList by lazy {
        convertFromJsonToListOf<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @Test
    fun `given PostDTO is input, should return PostEntity`() {

        val mapper = DTOtoEntityMapper()

        // GIVEN
        val expected = postEntityList

        // WHEN
        val actual = mapper.map(postDTOList)

        // THEN
        Truth.assertThat(actual).containsExactlyElementsIn(expected)
    }
}
